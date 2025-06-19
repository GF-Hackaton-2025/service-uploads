package br.com.uploads.app.usecases;

import br.com.uploads.app.ports.UploadQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static br.com.uploads.webui.constants.Constants.EMAIL_CONTEXT_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UploadUseCaseTest {

  @Mock
  private BucketUseCase bucketUseCase;
  @Mock
  private UploadQueue uploadQueue;
  @Mock
  private FilePart filePart1;
  @Mock
  private FilePart filePart2;

  @InjectMocks
  private UploadUseCase uploadUseCase;

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
  void uploadFiles_shouldUploadAllFilesAndSendQueueMessage() {
    when(filePart1.filename()).thenReturn("file1.txt");
    when(filePart2.filename()).thenReturn("file2.txt");
    when(bucketUseCase.uploadFile(filePart1)).thenReturn(Mono.just(filePart1));
    when(bucketUseCase.uploadFile(filePart2)).thenReturn(Mono.just(filePart2));
    when(uploadQueue.sendMessage(any())).thenReturn(Mono.just(createSendMessageResponse()));

    Flux<FilePart> files = Flux.just(filePart1, filePart2);
    Context context = Context.of(EMAIL_CONTEXT_KEY, "test@email.com");

    Mono<Void> result = uploadUseCase.uploadFiles(files).contextWrite(context);

    StepVerifier.create(result)
      .verifyComplete();
    verify(bucketUseCase, times(1)).uploadFile(filePart1);
    verify(bucketUseCase, times(1)).uploadFile(filePart2);
    verify(uploadQueue, times(1)).sendMessage(any());
  }

  @Test
  void uploadFiles_shouldHandleUploadFailureAndSendQueueMessage() {
    when(filePart1.filename()).thenReturn("file1.txt");
    when(filePart2.filename()).thenReturn("file2.txt");
    when(bucketUseCase.uploadFile(filePart1)).thenReturn(Mono.error(new RuntimeException("fail")));
    when(bucketUseCase.uploadFile(filePart2)).thenReturn(Mono.just(filePart2));
    when(uploadQueue.sendMessage(any())).thenReturn(Mono.just(createSendMessageResponse()));

    Flux<FilePart> files = Flux.just(filePart1, filePart2);
    Context context = Context.of(EMAIL_CONTEXT_KEY, "test@email.com");

    Mono<Void> result = uploadUseCase.uploadFiles(files).contextWrite(context);

    StepVerifier.create(result)
      .verifyComplete();

    verify(bucketUseCase, times(1)).uploadFile(filePart1);
    verify(bucketUseCase, times(1)).uploadFile(filePart2);
    verify(uploadQueue, times(1)).sendMessage(any());
  }

  @Test
  void uploadFiles_shouldSendQueueMessageWithEmptyListIfNoFiles() {
    when(uploadQueue.sendMessage(any())).thenReturn(Mono.just(createSendMessageResponse()));
    Flux<FilePart> files = Flux.empty();
    Context context = Context.of(EMAIL_CONTEXT_KEY, "test@email.com");

    Mono<Void> result = uploadUseCase.uploadFiles(files).contextWrite(context);

    StepVerifier.create(result)
      .verifyComplete();

    verify(bucketUseCase, times(0)).uploadFile(any());
    verify(uploadQueue, times(1)).sendMessage(any());
  }

  private SendMessageResponse createSendMessageResponse() {
    return SendMessageResponse.builder()
      .messageId("test-message-id")
      .build();
  }
}

