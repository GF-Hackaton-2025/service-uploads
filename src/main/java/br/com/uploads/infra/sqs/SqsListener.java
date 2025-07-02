package br.com.uploads.infra.sqs;

import br.com.uploads.app.usecases.ProcessFileUseCase;
import br.com.uploads.app.usecases.models.FileProcessorQueueMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import static br.com.uploads.utils.JsonUtils.fromJson;
import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsListener {

  @Setter
  @Value("${aws.sqs.fileProcessorQueueUrl}")
  private String fileProcessorQueueUrl;

  private final SqsAsyncClient sqsAsyncClient;
  private final ProcessFileUseCase processFileUseCase;

  @PostConstruct
  public void startListener() {
    var request = ReceiveMessageRequest.builder()
      .queueUrl(fileProcessorQueueUrl)
      .build();

    Mono.fromFuture(() -> sqsAsyncClient.receiveMessage(request))
      .repeat()
      .retry()
      .flatMapIterable(ReceiveMessageResponse::messages)
      .doOnNext(message -> log.info(format("Received messageId: %s, messageBody: %s", message.messageId(), message.body())))
      .flatMap(this::processMessage)
      .subscribeOn(Schedulers.boundedElastic())
      .subscribe();
  }

  private Mono<Void> processMessage(Message message) {
    return processFileUseCase.process(fromJson(message.body(), FileProcessorQueueMessage.class))
      .flatMap(m -> this.deleteMessage(message));
  }

  private Mono<Void> deleteMessage(Message message) {
    return Mono.just(sqsAsyncClient.deleteMessage(DeleteMessageRequest.builder()
        .queueUrl(fileProcessorQueueUrl)
        .receiptHandle(message.receiptHandle())
        .build()))
      .doOnNext(m -> log.info(format("Deleted messageId: %s, messageBody: %s", message.messageId(), message.body())))
      .then();
  }

}
