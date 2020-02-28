package info.devlink.microservices.composite.developer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.devlink.api.core.contact.Contact;
import info.devlink.api.core.contact.ContactService;
import info.devlink.api.core.developer.Developer;
import info.devlink.api.core.developer.DeveloperService;
import info.devlink.api.core.recruiter.Recruiter;
import info.devlink.api.core.recruiter.RecruiterService;
import info.devlink.api.event.Event;
import info.devlink.util.exceptions.InvalidInputException;
import info.devlink.util.exceptions.NotFoundException;
import info.devlink.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static info.devlink.api.event.Event.Type.CREATE;
import static info.devlink.api.event.Event.Type.DELETE;
import static reactor.core.publisher.Flux.empty;

@EnableBinding(DeveloperCompositeIntegration.MessageSources.class)
@Component
public class DeveloperCompositeIntegration implements DeveloperService, RecruiterService, ContactService {
    private static final Logger LOG = LoggerFactory.getLogger(DeveloperCompositeIntegration.class);

    private final WebClient webClient;
    private final ObjectMapper mapper;

    private final String developerServiceUrl;
    private final String recruiterServiceUrl;
    private final String contactServiceUrl;

    private MessageSources messageSources;

    public interface MessageSources {

        String OUTPUT_PRODUCTS = "output-developers";
        String OUTPUT_RECOMMENDATIONS = "output-recruiters";
        String OUTPUT_REVIEWS = "output-contacts";

        @Output(OUTPUT_PRODUCTS)
        MessageChannel outputDevelopers();

        @Output(OUTPUT_RECOMMENDATIONS)
        MessageChannel outputRecruiters();

        @Output(OUTPUT_REVIEWS)
        MessageChannel outputContacts();
    }

    @Autowired
    public DeveloperCompositeIntegration(
            WebClient.Builder webClient,
            ObjectMapper mapper,
            MessageSources messageSources,

            @Value("${app.developer-service.host}") String developerServiceHost,
            @Value("${app.developer-service.port}") int    developerServicePort,

            @Value("${app.recruiter-service.host}") String recruiterServiceHost,
            @Value("${app.recruiter-service.port}") int    recruiterServicePort,

            @Value("${app.contact-service.host}") String contactServiceHost,
            @Value("${app.contact-service.port}") int    contactServicePort
    ) {

        this.webClient = webClient.build();
        this.mapper = mapper;
        this.messageSources = messageSources;

        developerServiceUrl        = "http://" + developerServiceHost + ":" + developerServicePort;
        recruiterServiceUrl = "http://" + recruiterServiceHost + ":" + recruiterServicePort;
        contactServiceUrl         = "http://" + contactServiceHost + ":" + contactServicePort;
    }

    @Override
    public Developer createDeveloper(Developer body) {
        messageSources.outputDevelopers().send(MessageBuilder.withPayload(new Event(CREATE, body.getDeveloperId(), body)).build());
        return body;
    }

    @Override
    public Mono<Developer> getDeveloper(int developerId) {
        String url = developerServiceUrl + "/developer/" + developerId;
        LOG.debug("Will call the getDeveloper API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Developer.class).log().onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }

    @Override
    public void deleteDeveloper(int developerId) {
        messageSources.outputDevelopers().send(MessageBuilder.withPayload(new Event(DELETE, developerId, null)).build());
    }

    @Override
    public Recruiter createRecruiter(Recruiter body) {
        messageSources.outputRecruiters().send(MessageBuilder.withPayload(new Event(CREATE, body.getDeveloperId(), body)).build());
        return body;
    }

    @Override
    public Flux<Recruiter> getRecruiters(int developerId) {

        String url = recruiterServiceUrl + "/recruiter?developerId=" + developerId;

        LOG.debug("Will call the getRecruiters API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return webClient.get().uri(url).retrieve().bodyToFlux(Recruiter.class).log().onErrorResume(error -> empty());
    }

    @Override
    public void deleteRecruiters(int developerId) {
        messageSources.outputRecruiters().send(MessageBuilder.withPayload(new Event(DELETE, developerId, null)).build());
    }

    @Override
    public Contact createContact(Contact body) {
        messageSources.outputContacts().send(MessageBuilder.withPayload(new Event(CREATE, body.getDeveloperId(), body)).build());
        return body;
    }

    @Override
    public Flux<Contact> getContacts(int developerId) {

        String url = contactServiceUrl + "/contact?developerId=" + developerId;

        LOG.debug("Will call the getContacts API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return webClient.get().uri(url).retrieve().bodyToFlux(Contact.class).log().onErrorResume(error -> empty());

    }

    @Override
    public void deleteContacts(int developerId) {
        messageSources.outputContacts().send(MessageBuilder.withPayload(new Event(DELETE, developerId, null)).build());
    }

    public Mono<Health> getDeveloperHealth() {
        return getHealth(developerServiceUrl);
    }

    public Mono<Health> getRecruiterHealth() {
        return getHealth(recruiterServiceUrl);
    }

    public Mono<Health> getContactHealth() {
        return getHealth(contactServiceUrl);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log();
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}
