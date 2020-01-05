package dev.samsanders.poc.chatroom.app;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReactiveConfiguration {

    @Bean
    public RouterFunction<ServerResponse> reactiveRoutes(ChatMessageHandler chatMessageHandler) {
        final ClassPathResource indexHtml = new ClassPathResource("static/index.html");
        return RouterFunctions
                .route(RequestPredicates.POST("/chat-messages")
                                .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
                        chatMessageHandler::create)
                .andRoute(RequestPredicates.GET("/"), serverRequest ->
                        ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(BodyInserters.fromResource(indexHtml)));

    }

    @Bean
    public ChatMessageHandler chatMessageHandler(KinesisGateway kinesisGateway) {
        return new ChatMessageHandler(kinesisGateway);
    }

    @Bean
    public KinesisGateway kinesisGateway(KinesisProducer kinesisProducer,
                                         @Value("${app.stream.name}") String streamName) {
        return new KinesisGateway(kinesisProducer, streamName);
    }

    @Bean
    public KinesisProducer kinesisProducer(@Value("${aws.access.key}") String awsAccessKey,
                                           @Value("${aws.secret.key}") String awsSecretKey) {
        KinesisProducerConfiguration kinesisProducerConfiguration = new KinesisProducerConfiguration();
        kinesisProducerConfiguration.setRegion("us-west-1");

        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        kinesisProducerConfiguration.setCredentialsProvider(credentialsProvider);

        return new KinesisProducer(kinesisProducerConfiguration);
    }

}
