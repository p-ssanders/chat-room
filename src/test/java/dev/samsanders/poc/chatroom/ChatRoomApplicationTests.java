package dev.samsanders.poc.chatroom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.samsanders.poc.chatroom.app.ChatMessage;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.UnicastProcessor;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class ChatRoomApplicationTests {

    static {
        System.setProperty("aws.access.key", "some-aws-access-key");
        System.setProperty("aws.secret.key", "some-aws-secret-key");
        System.setProperty("app.stream.name", "some-stream-name");
    }

    @LocalServerPort
    int port;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    UnicastProcessor<ChatMessage> unicastProcessor;

    @MockBean
    KinesisProducer kinesisProducer;

    @Test
    void integration() throws URISyntaxException, JsonProcessingException {
        // Setup to skip persisting data in Amazon Kinesis and instead directly to the UnicastProcessor
        when(kinesisProducer.addUserRecord(any(), any(), any())).then(invocationOnMock -> {
            ByteBuffer actualByteBuffer = invocationOnMock.getArgument(2);
            String actualText = new String(actualByteBuffer.array());
            unicastProcessor.onNext(new ChatMessage(actualText));
            return null;
        });

        // Send a message via HTTP
        // Expect a 202 Accepted
        WebTestClient.bindToApplicationContext(applicationContext)
            .configureClient()
            .build()
            .post()
            .uri("/chat-messages")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new ChatMessage("some-text")))
            .exchange()
            .expectStatus()
            .isAccepted();

        // Also expect the KinesisProducer to be invoked with the correct message data
        String expectedChatMessageJson = new ObjectMapper().writeValueAsString(new ChatMessage("some-text"));
        ByteBuffer expected = ByteBuffer.wrap(expectedChatMessageJson.getBytes(StandardCharsets.UTF_8));
        verify(kinesisProducer).addUserRecord("some-stream-name", "1", expected);

        // Read from WebSocket
        // Expect to receive the one message sent by the KinesisProducer
        URI url = new URI(String.format("ws://localhost:%d/websockets/chat-messages", port));
        new ReactorNettyWebSocketClient()
            .execute(url, session ->
                session
                    .receive()
                    .next()
                    .map(WebSocketMessage::getPayloadAsText)
                    .doOnNext(actualChatMessageJson -> {
                        try {
                            ChatMessage actual =
                                new ObjectMapper().readerFor(ChatMessage.class).readValue(actualChatMessageJson);

                            assertEquals(new ChatMessage("some-text"), actual);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            fail();
                        }
                    })
                    .then()
            )
            .block(Duration.ofMillis(1000));
    }

}
