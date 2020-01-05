package dev.samsanders.poc.chatroom.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.UnicastProcessor;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

class ChatMessageRecordProcessorTest {

  @Test
  void processRecords() {
    UnicastProcessor<ChatMessage> unicastProcessor = UnicastProcessor.create();
    ChatMessageRecordProcessor chatMessageRecordProcessor = new ChatMessageRecordProcessor(unicastProcessor);
    KinesisClientRecord data = KinesisClientRecord.builder()
        .data(ByteBuffer.wrap("some-text".getBytes(StandardCharsets.UTF_8))).build();
    List<KinesisClientRecord> records = Collections.singletonList(data);
    ProcessRecordsInput input = ProcessRecordsInput.builder().records(records).build();

    chatMessageRecordProcessor.processRecords(input);

    ChatMessage actual = unicastProcessor.blockFirst(Duration.ofMillis(1000));
    assertEquals(new ChatMessage("some-text"), actual);
  }
}