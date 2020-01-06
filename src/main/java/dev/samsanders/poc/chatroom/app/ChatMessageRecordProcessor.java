package dev.samsanders.poc.chatroom.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.UnicastProcessor;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;

public class ChatMessageRecordProcessor implements ShardRecordProcessor {

    private final UnicastProcessor<ChatMessage> unicastProcessor;
    private final ObjectMapper objectMapper;

    public ChatMessageRecordProcessor(UnicastProcessor<ChatMessage> unicastProcessor) {
        this.unicastProcessor = unicastProcessor;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        processRecordsInput.records().forEach(kinesisClientRecord -> {
            try {
                byte[] arr = new byte[kinesisClientRecord.data().remaining()];
                kinesisClientRecord.data().get(arr);
                String json = new String(arr);
                ChatMessage chatMessage = objectMapper.reader().forType(ChatMessage.class).readValue(json);
                unicastProcessor.onNext(chatMessage);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
    }
}
