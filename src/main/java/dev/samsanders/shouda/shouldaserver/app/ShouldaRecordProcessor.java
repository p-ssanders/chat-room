package dev.samsanders.shouda.shouldaserver.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.UnicastProcessor;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;

public class ShouldaRecordProcessor implements ShardRecordProcessor {

    private final Logger log = LoggerFactory.getLogger(ShouldaRecordProcessor.class);
    private final UnicastProcessor<Shoulda> unicastProcessor;

    public ShouldaRecordProcessor(UnicastProcessor<Shoulda> unicastProcessor) {
        this.unicastProcessor = unicastProcessor;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        log.info("initialize!");
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        processRecordsInput.records().forEach(kinesisClientRecord -> {
            byte[] arr = new byte[kinesisClientRecord.data().remaining()];
            kinesisClientRecord.data().get(arr);
            String text = new String(arr);

            unicastProcessor.onNext(new Shoulda(text));
        });
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        log.info("lease lost!");
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        log.info("shard ended!");
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        log.info("shutdown requested! ");
    }
}
