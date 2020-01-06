package dev.samsanders.poc.chatroom.app;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import reactor.core.publisher.Mono;

public class KinesisGateway {
    private static final String PARTITION_KEY = "1";

    private final KinesisProducer kinesisProducer;
    private final String streamName;
    private final ObjectMapper objectMapper;

    public KinesisGateway(KinesisProducer kinesisProducer, String streamName) {
        this.kinesisProducer = kinesisProducer;
        this.streamName = streamName;
        this.objectMapper = new ObjectMapper();
    }

    public Mono<Void> putRecord(Mono<ChatMessage> chatMessageMono) {
        return chatMessageMono.doOnSuccess(chatMessage -> {
            try {
                String chatMessageJson = objectMapper.writeValueAsString(chatMessage);
                ByteBuffer wrap = ByteBuffer.wrap(chatMessageJson.getBytes(StandardCharsets.UTF_8));
                kinesisProducer.addUserRecord(streamName, PARTITION_KEY, wrap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }).then();
    }

}
