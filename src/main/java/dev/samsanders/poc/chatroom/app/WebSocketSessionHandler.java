package dev.samsanders.poc.chatroom.app;


import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebSocketSessionHandler implements WebSocketHandler {

    private final Flux<ChatMessage> chatMessageStream;

    public WebSocketSessionHandler(Flux<ChatMessage> chatMessageStream) {
        this.chatMessageStream = chatMessageStream;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(
                chatMessageStream.map(ChatMessage::getText)
                        .map(webSocketSession::textMessage)
        );
    }
}
