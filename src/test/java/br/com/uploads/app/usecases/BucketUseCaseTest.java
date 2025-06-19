package br.com.uploads.app.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static br.com.uploads.webui.constants.Constants.EMAIL_CONTEXT_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class BucketUseCaseTest {

  @Mock
  private S3Client s3Client;

  @Mock
  private FilePart filePart;

  @InjectMocks
  private BucketUseCase bucketUseCase;

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
  void uploadFile_shouldUploadAndReturnFilePart() {
    var dataBuffer = new DefaultDataBufferFactory().wrap("test-data".getBytes(StandardCharsets.UTF_8));
    when(filePart.content()).thenReturn(Flux.just(dataBuffer));
    when(filePart.filename()).thenReturn("file.txt");
    when(filePart.headers()).thenReturn(new org.springframework.http.HttpHeaders());
    when(s3Client.putObject((PutObjectRequest) any(), (RequestBody) any())).thenReturn(PutObjectResponse.builder().build());

    var context = Context.of(EMAIL_CONTEXT_KEY, "test@email.com");
    Mono<FilePart> result = bucketUseCase.uploadFile(filePart).contextWrite(context);

    StepVerifier.create(result)
      .expectNext(filePart)
      .verifyComplete();

    verify(s3Client, times(1)).putObject((PutObjectRequest) any(), (RequestBody) any());
  }

  @Test
  void uploadFile_shouldHandleMultipleDataBuffers() {
    var dataBuffer1 = new DefaultDataBufferFactory().wrap("part1".getBytes(StandardCharsets.UTF_8));
    var dataBuffer2 = new DefaultDataBufferFactory().wrap("part2".getBytes(StandardCharsets.UTF_8));
    when(filePart.content()).thenReturn(Flux.just(dataBuffer1, dataBuffer2));
    when(filePart.filename()).thenReturn("file.txt");
    when(filePart.headers()).thenReturn(new org.springframework.http.HttpHeaders());
    when(s3Client.putObject((PutObjectRequest) any(), (RequestBody) any())).thenReturn(PutObjectResponse.builder().build());

    var context = Context.of(EMAIL_CONTEXT_KEY, "test@email.com");
    Mono<FilePart> result = bucketUseCase.uploadFile(filePart).contextWrite(context);

    StepVerifier.create(result)
      .expectNext(filePart)
      .verifyComplete();
    verify(s3Client, times(1)).putObject((PutObjectRequest) any(), (RequestBody) any());
  }

  @Test
  void uploadFile_shouldHandleIOException() {
    var dataBuffer = new DefaultDataBufferFactory().wrap("test-data".getBytes(StandardCharsets.UTF_8));
    when(filePart.content()).thenReturn(Flux.just(dataBuffer));
    when(filePart.filename()).thenReturn("file.txt");
    when(filePart.headers()).thenReturn(new HttpHeaders());
    DataBuffer spyBuffer = spy(dataBuffer);
    doThrow(new RuntimeException(new IOException("IO error"))).when(spyBuffer).asByteBuffer();
    when(filePart.content()).thenReturn(Flux.just(spyBuffer));

    var context = Context.of(EMAIL_CONTEXT_KEY, "test@email.com");
    Mono<FilePart> result = bucketUseCase.uploadFile(filePart).contextWrite(context);

    StepVerifier.create(result)
      .expectError(RuntimeException.class)
      .verify();

    verify(s3Client, times(0)).putObject((PutObjectRequest) any(), (RequestBody) any());
  }

  @Test
  void uploadFile_shouldHandleS3PutObjectFailure() {
    var dataBuffer = new DefaultDataBufferFactory().wrap("test-data".getBytes(StandardCharsets.UTF_8));
    when(filePart.content()).thenReturn(Flux.just(dataBuffer));
    when(filePart.filename()).thenReturn("file.txt");
    when(filePart.headers()).thenReturn(new org.springframework.http.HttpHeaders());
    when(s3Client.putObject((PutObjectRequest) any(), (RequestBody) any())).thenThrow(new RuntimeException("S3 error"));

    var context = Context.of(EMAIL_CONTEXT_KEY, "test@email.com");
    Mono<FilePart> result = bucketUseCase.uploadFile(filePart).contextWrite(context);

    StepVerifier.create(result)
      .expectError(RuntimeException.class)
      .verify();

    verify(s3Client, times(1)).putObject((PutObjectRequest) any(), (RequestBody) any());
  }

  @Test
  void uploadFile_shouldErrorWhenEmailContextKeyMissing() {
    var dataBuffer = new DefaultDataBufferFactory().wrap("test-data".getBytes(StandardCharsets.UTF_8));
    when(filePart.content()).thenReturn(Flux.just(dataBuffer));
    when(filePart.filename()).thenReturn("file.txt");
    when(filePart.headers()).thenReturn(new HttpHeaders());

    Mono<FilePart> result = bucketUseCase.uploadFile(filePart);

    StepVerifier.create(result)
      .expectError(Throwable.class)
      .verify();

    verify(s3Client, times(0)).putObject((PutObjectRequest) any(), (RequestBody) any());
  }

  @Test
  void uploadFile_shouldNotCallS3ForEmptyFile() {
    when(filePart.content()).thenReturn(Flux.empty());
    when(filePart.filename()).thenReturn("file.txt");
    when(filePart.headers()).thenReturn(new HttpHeaders());
    var context = Context.of(EMAIL_CONTEXT_KEY, "test@email.com");
    Mono<FilePart> result = bucketUseCase.uploadFile(filePart).contextWrite(context);

    StepVerifier.create(result)
      .expectComplete()
      .verify();

    verify(s3Client, times(0)).putObject((PutObjectRequest) any(), (RequestBody) any());
  }
}
