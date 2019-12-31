package dev.samsanders.shouda.shouldaserver.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShouldaHandlerTest {

    @Mock
    private KinesisGateway kinesisGateway;

    @Test
    void create() {
//        final ShouldaHandler shouldaHandler = new ShouldaHandler(kinesisGateway);
//        Mono<String> body = Mono.just("hi");
//        ServerRequest request = MockServerRequest.builder().body(body);
//
//        final Mono<ServerResponse> serverResponseMono = shouldaHandler.create(request);
//
//        assertEquals(HttpStatus.ACCEPTED, serverResponseMono.block(Duration.ofMillis(1000)).statusCode());
//        verify(kinesisGateway).putRecord("hi");
    }
}