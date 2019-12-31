package dev.samsanders.shouda.shouldaserver.app;


import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ShouldaHandler {

    private final KinesisGateway kinesisGateway;

    public ShouldaHandler(KinesisGateway kinesisGateway) {
        this.kinesisGateway = kinesisGateway;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<Shoulda> shouldaMono = request.bodyToMono(Shoulda.class);

        return ServerResponse.accepted().body(kinesisGateway.putRecord(shouldaMono), Void.class);
    }
}
