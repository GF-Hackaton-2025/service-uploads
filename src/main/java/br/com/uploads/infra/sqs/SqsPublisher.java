package br.com.uploads.infra.sqs;

import br.com.uploads.app.ports.UploadQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static br.com.uploads.webui.constants.Constants.EMAIL_CONTEXT_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsPublisher implements UploadQueue {

  private final SqsAsyncClient sqsClient;

  @Value("${aws.sqs.endpoint}")
  private String queueUrl;

  @Override
  public Mono<SendMessageResponse> sendMessage(String body) {
    return Mono.deferContextual(ctx -> {
      var email = ctx.get(EMAIL_CONTEXT_KEY);
      var request = createSendMessageRequest(body);
      log.info(String.format("Sending message to SQS. User: %s Body: %s", email, request.messageBody()));
      return Mono.fromFuture(() -> sqsClient.sendMessage(request))
        .doOnSuccess(response -> log.info(String.format("Message sent successfully with ID: %s. User: %s", response.messageId(), email)))
        .doOnError(error -> log.error(String.format("Error sending message to SQS. User: %s", email), error));
    });
  }

  private SendMessageRequest createSendMessageRequest(String body) {
    return SendMessageRequest.builder()
      .queueUrl(queueUrl)
      .messageBody(body)
      .build();
  }

}
