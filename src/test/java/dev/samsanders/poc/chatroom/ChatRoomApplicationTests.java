package dev.samsanders.poc.chatroom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ChatRoomApplicationTests {

    static {
        System.setProperty("aws.access.key", "some-aws-access-key");
        System.setProperty("aws.secret.key", "some-aws-secret-key");
    }

    @Test
    void contextLoads() {

    }

//    @LocalServerPort
//    int port;
//
//    @Autowired
//    ApplicationContext applicationContext;
//
//    WebTestClient client;
//
//    @BeforeEach
//    void setUp() {
//        this.client = WebTestClient.bindToApplicationContext(applicationContext)
//                .configureClient()
//                .baseUrl("/messages")
//                .build();
//    }
//
//    @Test
//    @Ignore
//    void create() {
//        this.client
//                .post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(new Message("some-text")))
//                .exchange()
//                .expectStatus().isAccepted();
//    }
//
//    @Test
//    @Ignore
//    void read() throws URISyntaxException {
//        WebSocketClient client = new ReactorNettyWebSocketClient();
//
//        URI url = new URI(String.format("ws://localhost:%d/ws/shouldas", port));
//        client.execute(url, session ->
//                session.receive()
//                        .doOnNext(webSocketMessage -> {
//                            System.out.println(webSocketMessage.getPayloadAsText());
//                        })
//                        .then()).block(Duration.ofMillis(1000));
//    }

}
