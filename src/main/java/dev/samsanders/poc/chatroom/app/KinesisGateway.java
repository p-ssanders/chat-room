package dev.samsanders.poc.chatroom.app;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class KinesisGateway {
    private static final String PARTITION_KEY = "1";

    private final KinesisProducer kinesisProducer;
    private final String streamName;

    public KinesisGateway(KinesisProducer kinesisProducer, String streamName) {
        this.kinesisProducer = kinesisProducer;
        this.streamName = streamName;
    }

    public Mono<Void> putRecord(Mono<ChatMessage> shouldaMono) {
        return shouldaMono.doOnSuccess(shoulda -> {
            try {
                // TODO should persist as JSON, not just the getText()
                ByteBuffer wrap = ByteBuffer.wrap(shoulda.getText().getBytes("UTF-8"));
                kinesisProducer.addUserRecord(streamName, PARTITION_KEY, wrap);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }).then();
    }

}
