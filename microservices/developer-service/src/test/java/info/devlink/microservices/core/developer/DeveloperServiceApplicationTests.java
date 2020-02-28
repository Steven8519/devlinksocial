package info.devlink.microservices.core.developer;

import info.devlink.api.core.developer.Developer;
import info.devlink.api.event.Event;
import info.devlink.microservices.core.developer.repository.DeveloperRepository;
import info.devlink.util.exceptions.InvalidInputException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static info.devlink.api.event.Event.Type.CREATE;
import static info.devlink.api.event.Event.Type.DELETE;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class DeveloperServiceApplicationTests {
    @Autowired
    private WebTestClient client;

    @Autowired
    private DeveloperRepository repository;

    @Autowired
    private Sink channels;

    private AbstractMessageChannel input = null;

    @Before
    public void setupDb() {
        input = (AbstractMessageChannel) channels.input();
        repository.deleteAll().block();
    }

    @Test
    public void getDeveloperById() {

        int developerId = 1;

        assertNull(repository.findByDeveloperId(developerId).block());
        assertEquals(0, (long)repository.count().block());

        sendCreateDeveloperEvent(developerId);

        assertNotNull(repository.findByDeveloperId(developerId).block());
        assertEquals(1, (long)repository.count().block());

        getAndVerifyDeveloper(developerId, OK)
                .jsonPath("$.developerId").isEqualTo(developerId);
    }

    @Test
    public void duplicateError() {

        int developerId = 1;

        assertNull(repository.findByDeveloperId(developerId).block());

        sendCreateDeveloperEvent(developerId);

        assertNotNull(repository.findByDeveloperId(developerId).block());

        try {
            sendCreateDeveloperEvent(developerId);
            fail("Expected a MessagingException here!");
        } catch (MessagingException me) {
            if (me.getCause() instanceof InvalidInputException)	{
                InvalidInputException iie = (InvalidInputException)me.getCause();
                assertEquals("Duplicate key, Developer Id: " + developerId, iie.getMessage());
            } else {
                fail("Expected a InvalidInputException as the root cause!");
            }
        }
    }

    @Test
    public void deleteDeveloper() {

        int developerId = 1;

        sendCreateDeveloperEvent(developerId);
        assertNotNull(repository.findByDeveloperId(developerId).block());

        sendDeleteDeveloperEvent(developerId);
        assertNull(repository.findByDeveloperId(developerId).block());

        sendDeleteDeveloperEvent(developerId);
    }

    @Test
    public void getDeveloperInvalidParameterString() {

        getAndVerifyDeveloper("/no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/developer/no-integer")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getDeveloperNotFound() {

        int developerIdNotFound = 13;
        getAndVerifyDeveloper(developerIdNotFound, NOT_FOUND)
                .jsonPath("$.path").isEqualTo("/developer/" + developerIdNotFound)
                .jsonPath("$.message").isEqualTo("No developer found for developerId: " + developerIdNotFound);
    }

    @Test
    public void getDeveloperInvalidParameterNegativeValue() {

        int developerIdInvalid = -1;

        getAndVerifyDeveloper(developerIdInvalid, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/developer/" + developerIdInvalid)
                .jsonPath("$.message").isEqualTo("Invalid developerId: " + developerIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyDeveloper(int developerId, HttpStatus expectedStatus) {
        return getAndVerifyDeveloper("/" + developerId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyDeveloper(String developerIdPath, HttpStatus expectedStatus) {
        return client.get()
                .uri("/developer" + developerIdPath)
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody();
    }

    private void sendCreateDeveloperEvent(int developerId) {
        Developer developer = new Developer(developerId, "Chris", "Harris" + developerId, "Unknown", "SA");
        Event<Integer, Developer> event = new Event(CREATE, developerId, developer);
        input.send(new GenericMessage<>(event));
    }

    private void sendDeleteDeveloperEvent(int developerId) {
        Event<Integer, Developer> event = new Event(DELETE, developerId, null);
        input.send(new GenericMessage<>(event));
    }
}
