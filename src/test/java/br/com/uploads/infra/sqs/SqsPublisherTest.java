package br.com.uploads.infra.sqs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static br.com.uploads.webui.constants.Constants.EMAIL_CONTEXT_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class SqsPublisherTest {

  @Mock
  private SqsClient sqsClient;

  @InjectMocks
  private SqsPublisher sqsPublisher;

  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = openMocks(this);
  }

  @AfterEach
  void closeService() throws Exception {
    closeable.close();
  }

  @Test
  void sendMessage_shouldSendMessageAndReturnResponse() {
    SendMessageResponse response = SendMessageResponse.builder().messageId("123").build();
    when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(response);

    var context = reactor.util.context.Context.of(EMAIL_CONTEXT_KEY, "test@email.com");
    Mono<SendMessageResponse> result = sqsPublisher.sendMessage("body").contextWrite(context);

    SendMessageResponse actual = result.block();
    assertNotNull(actual);
    assertEquals("123", actual.messageId());

    verify(sqsClient, times(1)).sendMessage(any(SendMessageRequest.class));
  }

}
