package dev.samsanders.shouda.shouldaserver.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.util.stream.Collectors;

public class ShouldaRecordProcessor implements ShardRecordProcessor {

    private final Logger log = LoggerFactory.getLogger(ShouldaRecordProcessor.class);

    @Override
    public void initialize(InitializationInput initializationInput) {
        log.info("intialize!");
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        // TODO publish to websocket
        log.info("prcoess record! " + processRecordsInput.records().stream().map(KinesisClientRecord::sequenceNumber).collect(Collectors.joining()));
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        log.info("lease lost! " + leaseLostInput);
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        log.info("shard ended! " + shardEndedInput);
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        log.info("shutdown requested! " + shutdownRequestedInput);
    }
}
