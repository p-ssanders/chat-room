package dev.samsanders.poc.chatroom.app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ChatMessageHandlerTest {

  WebTestClient client;

  @Mock
  KinesisGateway kinesisGateway;

  @BeforeEach
  void setUp() {
    when(kinesisGateway.putRecord(any())).thenReturn(Mono.empty());
    ChatMessageHandler chatMessageHandler = new ChatMessageHandler(kinesisGateway);

    RouterFunction<ServerResponse> route = RouterFunctions
        .route(RequestPredicates.POST("/chat-messages")
                .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
            chatMessageHandler::create);

    client = WebTestClient.bindToRouterFunction(route).configureClient().build();
  }

  @Test
  void create() {
    client
        .post()
        .uri("/chat-messages")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new ChatMessage("some-text")))
        .exchange()
        .expectStatus()
        .isAccepted();

    verify(kinesisGateway).putRecord(any()); // TODO how to improve granularity?
  }
}