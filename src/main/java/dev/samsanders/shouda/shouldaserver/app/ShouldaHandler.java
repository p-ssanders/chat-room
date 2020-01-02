package dev.samsanders.shouda.shouldaserver.app;


import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ShouldaHandler implements WebSocketHandler {

    private final KinesisGateway kinesisGateway;
    private final Flux<Shoulda> shouldaStream;

    public ShouldaHandler(KinesisGateway kinesisGateway, Flux<Shoulda> shouldaStream) {
        this.kinesisGateway = kinesisGateway;
        this.shouldaStream = shouldaStream;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<Shoulda> shouldaMono = request.bodyToMono(Shoulda.class);

        return ServerResponse.accepted().body(kinesisGateway.putRecord(shouldaMono), Void.class);
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(
                shouldaStream.map(Shoulda::getText)
                        .map(webSocketSession::textMessage)
        );
    }
}
