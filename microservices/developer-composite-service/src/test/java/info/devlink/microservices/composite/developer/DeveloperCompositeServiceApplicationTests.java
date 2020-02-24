package info.devlink.microservices.composite.developer;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

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
    public void getDeveloperById() {
        client.get()
                .uri("/developer-composite/" + DEVELOPER_ID_OK)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.developerId").isEqualTo(DEVELOPER_ID_OK);
    }

    @Test
    public void getDeveloperNotFound() {

        client.get()
                .uri("/developer-composite/" + DEVELOPER_ID_NOT_FOUND)
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/developer-composite/" + DEVELOPER_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("NOT FOUND: " + DEVELOPER_ID_NOT_FOUND);
    }

    @Test
    public void getDeveloperInvalidInput() {

        client.get()
                .uri("/developer-composite/" + DEVELOPER_ID_INVALID)
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/developer-composite/" + DEVELOPER_ID_INVALID)
                .jsonPath("$.message").isEqualTo("INVALID: " + DEVELOPER_ID_INVALID);
    }

}
