package dev.samsanders.poc.chatroom.app;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class KinesisGateway {
    private static final Logger logger = LoggerFactory.getLogger(KinesisGateway.class);
    private static final String PARTITION_KEY = "1";

    private final KinesisProducer kinesisProducer;
    private final String streamName;

    public KinesisGateway(KinesisProducer kinesisProducer, String streamName) {
        this.kinesisProducer = kinesisProducer;
        this.streamName = streamName;
    }

    public Mono<Void> putRecord(Mono<ChatMessage> chatMessageMono) {
        return chatMessageMono.doOnSuccess(chatMessage -> {
            // TODO should persist as JSON, not just the getText()
            ByteBuffer wrap = ByteBuffer.wrap(chatMessage.getText().getBytes(StandardCharsets.UTF_8));
            kinesisProducer.addUserRecord(streamName, PARTITION_KEY, wrap);
        }).then();
    }

}
