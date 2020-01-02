package dev.samsanders.poc.chatroom.app;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ChatMessageHandler {

    private final KinesisGateway kinesisGateway;

    public ChatMessageHandler(KinesisGateway kinesisGateway) {
        this.kinesisGateway = kinesisGateway;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<ChatMessage> requestBody = request.bodyToMono(ChatMessage.class);

        Mono<Void> putRecord = kinesisGateway.putRecord(requestBody);

        return ServerResponse.accepted().body(putRecord, Void.class);
    }

}
