package info.devlink.microservices.composite.developer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.devlink.api.core.contact.Contact;
import info.devlink.api.core.contact.ContactService;
import info.devlink.api.core.developer.Developer;
import info.devlink.api.core.developer.DeveloperService;
import info.devlink.api.core.recruiter.Recruiter;
import info.devlink.api.core.recruiter.RecruiterService;
import info.devlink.util.exceptions.InvalidInputException;
import info.devlink.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import info.devlink.util.http.HttpErrorInfo;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class DeveloperCompositeIntegration implements DeveloperService, RecruiterService, ContactService {

    private static final Logger LOG = LoggerFactory.getLogger(DeveloperCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String developerServiceUrl;
    private final String recruiterServiceUrl;
    private final String contactServiceUrl;

    @Autowired
    public DeveloperCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.developer-service.host}") String developerServiceHost,
            @Value("${app.developer-service.port}") int    developerServicePort,

            @Value("${app.recruiter-service.host}") String recruiterServiceHost,
            @Value("${app.recruiter-service.port}") int    recruiterServicePort,

            @Value("${app.contact-service.host}") String contactServiceHost,
            @Value("${app.contact-service.port}") int    contactServicePort
    ) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        developerServiceUrl        = "http://" + developerServiceHost + ":" + developerServicePort + "/developer/";
        recruiterServiceUrl = "http://" + recruiterServiceHost + ":" + recruiterServicePort + "/recruiter?developerId=";
        contactServiceUrl         = "http://" + contactServiceHost + ":" + contactServicePort + "/contact?developerId=";
    }

    public Developer getDeveloper(int developerId) {

        try {
            String url = developerServiceUrl + developerId;
            LOG.debug("Will call getDeveloper API on URL: {}", url);

            Developer developer = restTemplate.getForObject(url, Developer.class);
            LOG.debug("Found a developer with id: {}", developer.getDeveloperId());

            return developer;

        } catch (HttpClientErrorException ex) {

            switch (ex.getStatusCode()) {

                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));

                case UNPROCESSABLE_ENTITY :
                    throw new InvalidInputException(getErrorMessage(ex));

                default:
                    LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    public List<Recruiter> getRecruiters(int developerId) {

        try {
            String url = recruiterServiceUrl + developerId;

            LOG.debug("Will call getRecruiters API on URL: {}", url);
            List<Recruiter> recruiters = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Recruiter>>() {}).getBody();

            LOG.debug("Found {} recruiters for a developer with id: {}", recruiters.size(), developerId);
            return recruiters;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting recruiters, return zero recruiters: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Contact> getContacts(int developerId) {

        try {
            String url = contactServiceUrl + developerId;

            LOG.debug("Will call getContacts API on URL: {}", url);
            List<Contact> contacts = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Contact>>() {}).getBody();

            LOG.debug("Found {} contacts for a developer with id: {}", contacts.size(), developerId);
            return contacts;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting contacts, return zero contacts: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

}
