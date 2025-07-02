package br.com.uploads.app.usecases;

import br.com.uploads.app.entities.File;
import br.com.uploads.app.repositories.FilesRepository;
import br.com.uploads.app.usecases.models.FileProcessorQueueMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;

import static br.com.uploads.enums.FileStatusEnum.FAILED;
import static br.com.uploads.enums.FileStatusEnum.PROCESSED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ProcessFileUseCaseTest {

  @Mock
  private BucketUseCase bucketUseCase;

  @Mock
  private EmailUseCase emailUseCase;

  @Mock
  private FilesRepository filesRepository;

  @InjectMocks
  private ProcessFileUseCase processFileUseCase;

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
  void shouldProcessSuccessFile() {
    var message = FileProcessorQueueMessage.builder()
      .email("test@email.com")
      .zipFileName("arquivo.zip")
      .fileId("123")
      .status(PROCESSED)
      .build();

    var file = new File();
    file.setFileId("123");
    file.setFileName("arquivo.zip");

    Path mockPath = Path.of("mock/path/arquivo.zip");

    when(bucketUseCase.getFile(any(), any(), any())).thenReturn(Mono.just(mockPath));
    when(filesRepository.findByFileId("123")).thenReturn(Mono.just(file));
    when(filesRepository.save(any())).thenReturn(Mono.just(file));
    when(emailUseCase.sendEmail(any(), any(), any(), any())).thenReturn(Mono.empty());

    StepVerifier.create(processFileUseCase.process(message))
      .expectNext(message)
      .verifyComplete();

    verify(bucketUseCase).getFile(any(), any(), any());
    verify(filesRepository).findByFileId("123");
    verify(filesRepository).save(any());
    verify(emailUseCase).sendEmail(eq("test@email.com"), any(), contains("Success"), any());
  }

  @Test
  void shouldProcessErrorFile() {
    var message = FileProcessorQueueMessage.builder()
      .email("error@email.com")
      .zipFileName("erro.zip")
      .fileId("456")
      .status(FAILED)
      .build();

    var file = new File();
    file.setFileId("456");
    file.setFileName("erro.zip");

    when(filesRepository.findByFileId("456")).thenReturn(Mono.just(file));
    when(filesRepository.save(any())).thenReturn(Mono.just(file));
    when(emailUseCase.sendEmail(any(), any(), any(), any())).thenReturn(Mono.empty());

    StepVerifier.create(processFileUseCase.process(message))
      .expectNext(message)
      .verifyComplete();

    verify(bucketUseCase, never()).getFile(any(), any(), any());
    verify(filesRepository).findByFileId("456");
    verify(filesRepository).save(any());
    verify(emailUseCase).sendEmail(eq("error@email.com"), any(), contains("Error"), isNull());
  }

}
