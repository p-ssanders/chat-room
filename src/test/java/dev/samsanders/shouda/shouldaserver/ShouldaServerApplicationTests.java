package dev.samsanders.shouda.shouldaserver;

import dev.samsanders.shouda.shouldaserver.app.Shoulda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest
@ActiveProfiles("test")
class ShouldaServerApplicationTests {

    static {
        System.setProperty("aws.access.key", "some-aws-access-key");
        System.setProperty("aws.secret.key", "some-aws-secret-key");
    }

    @Autowired
    ApplicationContext applicationContext;

    WebTestClient client;

    @BeforeEach
    void setUp() {
        this.client = WebTestClient.bindToApplicationContext(applicationContext)
                .configureClient()
                .baseUrl("/shouldas")
                .build();
    }


    @Test
	void contextLoads() {
	}

    @Test
    void create() {
        this.client
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new Shoulda("some-text")))
                .exchange()
                .expectStatus().isAccepted();
    }

}
