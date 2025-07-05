package br.com.uploads.webui.controllers;

import br.com.uploads.app.usecases.UploadUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UploadControllerTest {

  private UploadUseCase uploadUseCase;
  private UploadController uploadController;

  private static final String EMAIL = "test@example.com";

  @BeforeEach
  void setUp() {
    uploadUseCase = mock(UploadUseCase.class);
    uploadController = new UploadController(uploadUseCase);
  }

  @Test
  void uploadFiles_shouldCallUseCaseAndReturnMonoEmpty() {
    FilePart filePart1 = mock(FilePart.class);
    FilePart filePart2 = mock(FilePart.class);
    Flux<FilePart> files = Flux.just(filePart1, filePart2);

    when(uploadUseCase.uploadFiles(any()))
      .thenReturn(Mono.empty());

    Mono<Void> result = uploadController.uploadFiles(EMAIL, files);

    StepVerifier.create(result)
      .verifyComplete();

    verify(uploadUseCase, times(1)).uploadFiles(any());
  }

  @Test
  void uploadFiles_shouldHandleEmptyFiles() {
    Flux<FilePart> files = Flux.empty();

    when(uploadUseCase.uploadFiles(any()))
      .thenReturn(Mono.empty());

    Mono<Void> result = uploadController.uploadFiles(EMAIL, files);

    StepVerifier.create(result)
      .verifyComplete();

    verify(uploadUseCase, times(1)).uploadFiles(any());
  }
}