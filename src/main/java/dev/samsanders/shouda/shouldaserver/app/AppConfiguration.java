package dev.samsanders.shouda.shouldaserver.app;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClientBuilder;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class AppConfiguration {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent applicationReadyEvent) {
        final ConfigurableApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        final Scheduler scheduler = applicationContext.getBean(Scheduler.class);
        final TaskExecutor taskExecutor = applicationContext.getBean(TaskExecutor.class);

        taskExecutor.execute(scheduler);
    }

    @Bean("taskExecutor")
    @Profile("test")
    public TaskExecutor testTaskExecutor() {
        return runnable -> {
            // do nothing
        };
    }

    @Bean
    public RouterFunction<ServerResponse> reactiveRoutes(ShouldaHandler shouldaHandler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/shouldas")
                                .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
                        shouldaHandler::create);

    }

    @Bean
    public HandlerMapping webSocketRoutes(ShouldaHandler shouldaHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/sockets/shouldas", shouldaHandler);
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public ShouldaHandler shouldaHandler(KinesisGateway kinesisGateway) {
        return new ShouldaHandler(kinesisGateway);
    }

    @Bean
    @Profile("!test")
    public KinesisGateway kinesisGateway(KinesisProducer kinesisProducer,
                                         @Value("${app.stream.name}") String streamName) {
        return new KinesisGateway(kinesisProducer, streamName);
    }

    @Bean("kinesisGateway")
    @Profile("test")
    public KinesisGateway testKinesisGateway() {
        return new KinesisGateway(null, null) {

            @Override
            public Mono<Void> putRecord(Mono<Shoulda> shouldaMono) {
                return Mono.empty();
            }
        };
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

    @Bean
    public Scheduler kinesisScheduler(@Value("${app.name}") String applicationName,
                                      @Value("${app.stream.name}") String streamName,
                                      KinesisAsyncClient kinesisClient) {
        Region region = Region.US_WEST_1;
        DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.builder().region(region).build();
        CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder().region(region).build();

        ConfigsBuilder configsBuilder = new ConfigsBuilder(streamName, applicationName, kinesisClient, dynamoClient, cloudWatchClient, UUID.randomUUID().toString(), ShouldaRecordProcessor::new);

        return new Scheduler(
                configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                configsBuilder.leaseManagementConfig(),
                configsBuilder.lifecycleConfig(),
                configsBuilder.metricsConfig(),
                configsBuilder.processorConfig(),
                configsBuilder.retrievalConfig()
        );
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