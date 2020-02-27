package info.devlink.microservices.composite.developer;

import info.devlink.api.composite.ContactSummary;
import info.devlink.api.composite.DeveloperAggregate;
import info.devlink.api.composite.RecruiterSummary;
import info.devlink.api.core.contact.Contact;
import info.devlink.api.core.developer.Developer;
import info.devlink.api.core.recruiter.Recruiter;
import info.devlink.microservices.composite.developer.service.DeveloperCompositeIntegration;
import info.devlink.util.exceptions.InvalidInputException;
import info.devlink.util.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeveloperCompositeServiceApplicationTests {

    private static final int DEVELOPER_ID_OK = 1;
    private static final int DEVELOPER_ID_NOT_FOUND = 2;
    private static final int DEVELOPER_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @MockBean
    private DeveloperCompositeIntegration compositeIntegration;

    @Before
    public void setUp() {

        when(compositeIntegration.getDeveloper(DEVELOPER_ID_OK)).
                thenReturn(new Developer(DEVELOPER_ID_OK, "Mark", "Hill", "Software Engineer", "mock-address"));
        when(compositeIntegration.getContacts(DEVELOPER_ID_OK)).
                thenReturn(Collections.singletonList(new Contact(DEVELOPER_ID_OK, 1, 1, "sample@gmail.com", "312-555-5555", "mock address")));
        when(compositeIntegration.getRecruiters(DEVELOPER_ID_OK)).
                thenReturn(Collections.singletonList(new Recruiter(DEVELOPER_ID_OK, 1, "Karen Haines", "Job Spring Partners", "4",  "mock address")));

        when(compositeIntegration.getDeveloper(DEVELOPER_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + DEVELOPER_ID_NOT_FOUND));

        when(compositeIntegration.getDeveloper(DEVELOPER_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + DEVELOPER_ID_INVALID));
    }

    @Test
    public void contextLoads() {
    }


    @Test
    public void createCompositeDeveloper1() {

    }

    @Test
    public void createCompositeDeveloper2() {

    }

    @Test
    public void deleteCompositeDeveloper() {
    }

    @Test
    public void getDeveloperById() {

        getAndVerifyDeveloper(DEVELOPER_ID_OK, OK)
                .jsonPath("$.developerId").isEqualTo(DEVELOPER_ID_OK)
                .jsonPath("$.recruiters.length()").isEqualTo(1)
                .jsonPath("$.contacts.length()").isEqualTo(1);
    }

    @Test
    public void getDeveloperNotFound() {

        getAndVerifyDeveloper(DEVELOPER_ID_NOT_FOUND, NOT_FOUND)
                .jsonPath("$.path").isEqualTo("/developer-composite/" + DEVELOPER_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("NOT FOUND: " + DEVELOPER_ID_NOT_FOUND);
    }

    @Test
    public void getDeveloperInvalidInput() {

        getAndVerifyDeveloper(DEVELOPER_ID_INVALID, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/developer-composite/" + DEVELOPER_ID_INVALID)
                .jsonPath("$.message").isEqualTo("INVALID: " + DEVELOPER_ID_INVALID);
    }

    private WebTestClient.BodyContentSpec getAndVerifyDeveloper(int developerId, HttpStatus expectedStatus) {
        return client.get()
                .uri("/developer-composite/" + developerId)
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody();
    }

    private void postAndVerifyDeveloper(DeveloperAggregate compositeDeveloper, HttpStatus expectedStatus) {
        client.post()
                .uri("/developer-composite")
                .body(just(compositeDeveloper), null)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteAndVerifyDeveloper(int developerId, HttpStatus expectedStatus) {
        client.delete()
                .uri("/developer-composite/" + developerId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

}
