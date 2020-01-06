package dev.samsanders.poc.chatroom.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  void processRecords() throws JsonProcessingException {
    UnicastProcessor<ChatMessage> unicastProcessor = UnicastProcessor.create();
    ChatMessageRecordProcessor chatMessageRecordProcessor = new ChatMessageRecordProcessor(unicastProcessor);

    String json = new ObjectMapper().writeValueAsString(new ChatMessage("some-text"));
    ByteBuffer byteBuffer = ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8));
    KinesisClientRecord kinesisClientRecord = KinesisClientRecord.builder().data(byteBuffer).build();
    List<KinesisClientRecord> kinesisClientRecords = Collections.singletonList(kinesisClientRecord);
    ProcessRecordsInput processRecordsInput = ProcessRecordsInput.builder().records(kinesisClientRecords).build();

    chatMessageRecordProcessor.processRecords(processRecordsInput);

    ChatMessage actual = unicastProcessor.blockFirst(Duration.ofMillis(1000));
    assertEquals(new ChatMessage("some-text"), actual);
  }
}