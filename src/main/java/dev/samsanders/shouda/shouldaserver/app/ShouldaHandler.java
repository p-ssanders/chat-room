package dev.samsanders.shouda.shouldaserver.app;


import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public class ShouldaHandler implements WebSocketHandler {

    private final KinesisGateway kinesisGateway;

    public ShouldaHandler(KinesisGateway kinesisGateway) {
        this.kinesisGateway = kinesisGateway;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<Shoulda> shouldaMono = request.bodyToMono(Shoulda.class);

        return ServerResponse.accepted().body(kinesisGateway.putRecord(shouldaMono), Void.class);
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        DataBufferFactory dataBufferFactory = webSocketSession.bufferFactory();
        return webSocketSession.send(Mono.just(new WebSocketMessage(WebSocketMessage.Type.TEXT, dataBufferFactory.wrap("test 1".getBytes())))).then();
    }
}
