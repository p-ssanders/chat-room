package dev.samsanders.poc.chatroom.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientAsyncConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClientBuilder;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class WebSocketConfiguration {

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public HandlerMapping webSocketRoutes(WebSocketSessionHandler webSocketSessionHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/websockets/chat-messages", webSocketSessionHandler);

        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping(map);
        simpleUrlHandlerMapping.setOrder(-1);

        return simpleUrlHandlerMapping;
    }

    @Bean
    public WebSocketSessionHandler webSocketSessionHandler(Flux<ChatMessage> chatMessageStream) {
        return new WebSocketSessionHandler(chatMessageStream);
    }

    @Bean
    public Flux<ChatMessage> chatMessageStream(UnicastProcessor<ChatMessage> unicastProcessor,
                                               @Value("${app.cache.size}") int cacheSizeInNumberOfMessages) {
        return unicastProcessor.replay(cacheSizeInNumberOfMessages).autoConnect();
    }

    @Bean
    public Scheduler kinesisScheduler(@Value("${app.name}") String applicationName,
                                      @Value("${app.stream.name}") String streamName,
                                      KinesisAsyncClient kinesisClient,
                                      UnicastProcessor<ChatMessage> unicastProcessor) {
        Region region = Region.US_WEST_1;
        DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.builder().region(region).build();
        CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder().region(region).build();

        ConfigsBuilder configsBuilder = new ConfigsBuilder(
                streamName,
                applicationName,
                kinesisClient,
                dynamoClient,
                cloudWatchClient,
                UUID.randomUUID().toString(),
                () -> new ChatMessageRecordProcessor(unicastProcessor)
        );

        Scheduler scheduler = new Scheduler(
                configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                configsBuilder.leaseManagementConfig(),
                configsBuilder.lifecycleConfig(),
                configsBuilder.metricsConfig(),
                configsBuilder.processorConfig(),
                configsBuilder.retrievalConfig().initialPositionInStreamExtended(
                        InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.TRIM_HORIZON)
                )
        );

        return scheduler;
    }

    @Bean
    public UnicastProcessor<ChatMessage> unicastProcessor() {
        return UnicastProcessor.create();
    }

    @Bean
    public KinesisAsyncClient kinesisAsyncClient(@Value("${aws.access.key}") String awsAccessKey,
                                                 @Value("${aws.secret.key}") String awsSecretKey) {
        KinesisAsyncClientBuilder builder = KinesisAsyncClient.builder();
        builder.region(Region.US_WEST_1);

        final AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        builder.credentialsProvider(credentialsProvider);

        return KinesisClientUtil.createKinesisAsyncClient(builder);
    }
}
