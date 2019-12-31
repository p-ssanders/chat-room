package dev.samsanders.shouda.shouldaserver.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KinesisGatewayTest {

    @Mock
    KinesisClient kinesisClient;


    @Test
    void putRecord() {
//        PutRecordRequest expectedPutRecordRequest = PutRecordRequest.builder()
//                .streamName(KinesisGateway.STREAM_NAME)
//                .data(SdkBytes.fromUtf8String("some-record"))
//                .build();
//        KinesisGateway kinesisGateway = new KinesisGateway(kinesisClient);
//
//        kinesisGateway.putRecord("some-record");
//
//        verify(kinesisClient).putRecord(expectedPutRecordRequest);
    }
}