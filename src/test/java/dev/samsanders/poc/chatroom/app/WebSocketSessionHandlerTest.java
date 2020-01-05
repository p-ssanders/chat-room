package dev.samsanders.poc.chatroom.app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

class WebSocketSessionHandlerTest {

  @Test
  void handle() {
    Flux<ChatMessage> chatMessageStream = Flux.fromArray(new ChatMessage[] {
        new ChatMessage("some-text")
    });
    WebSocketSessionHandler webSocketSessionHandler = new WebSocketSessionHandler(chatMessageStream);
    WebSocketSession webSocketSession = mock(WebSocketSession.class);

    webSocketSessionHandler.handle(webSocketSession);

    verify(webSocketSession).send(any()); // TODO improve granularity
  }
}