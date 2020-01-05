package dev.samsanders.poc.chatroom.app;

import reactor.core.publisher.UnicastProcessor;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;

public class ChatMessageRecordProcessor implements ShardRecordProcessor {

    private final UnicastProcessor<ChatMessage> unicastProcessor;

    public ChatMessageRecordProcessor(UnicastProcessor<ChatMessage> unicastProcessor) {
        this.unicastProcessor = unicastProcessor;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        processRecordsInput.records().forEach(kinesisClientRecord -> {
            byte[] arr = new byte[kinesisClientRecord.data().remaining()];
            kinesisClientRecord.data().get(arr);
            String text = new String(arr);

            // TODO should deserialize from JSON not just text data
            unicastProcessor.onNext(new ChatMessage(text));
        });
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
