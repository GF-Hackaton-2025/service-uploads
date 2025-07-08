package br.com.uploads.app.usecases;

import br.com.uploads.app.entities.File;
import br.com.uploads.app.exception.BusinessException;
import br.com.uploads.app.repositories.FilesRepository;
import br.com.uploads.app.usecases.models.FileProcessorQueueMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;

import static br.com.uploads.enums.FileStatusEnum.PROCESSED;
import static br.com.uploads.utils.JsonUtils.toJson;
import static br.com.uploads.webui.constants.Constants.UPLOADS_BUCKET_NAME;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Path.of;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessFileUseCase {

  private final BucketUseCase bucketUseCase;
  private final EmailUseCase emailUseCase;
  private final FilesRepository filesRepository;

  public Mono<FileProcessorQueueMessage> process(FileProcessorQueueMessage message) {
    log.info("Processing message: {}", toJson(message));
    return Mono.just(message)
      .filter(m -> PROCESSED.equals(m.getStatus()))
      .flatMap(this::processSuccessFile)
      .switchIfEmpty(Mono.defer(() -> this.processErrorFile(message)));
  }

  private Mono<FileProcessorQueueMessage> processSuccessFile(FileProcessorQueueMessage message) {
    var key = message.getEmail() + "/" + message.getZipFileName();
    return Mono.just(message)
      .flatMap(file -> {
        try {
          var safeTempDir = createTempDirectory(of(getProperty("user.dir")), "processor_");
          return Mono.just(createTempFile(safeTempDir, "upload_", "_" + message.getZipFileName()));
        } catch (IOException e) {
          throw new BusinessException(e.getMessage());
        }
      })
      .flatMap(destinationPath -> this.bucketUseCase.getFile(UPLOADS_BUCKET_NAME, key, destinationPath)
        .flatMap(path -> this.filesRepository.findByFileId(message.getFileId())
          .flatMap(file -> this.filesRepository.save(updateFileEntity(file, message)))
          .doFinally(signal -> this.deleteTempFile(destinationPath))
          .flatMap(file -> emailUseCase.sendEmail(message.getEmail(), "File processor", format("Success to process file: %s", file.getFileName()), path))))
      .thenReturn(message);
  }

  private Mono<FileProcessorQueueMessage> processErrorFile(FileProcessorQueueMessage message) {
    return this.filesRepository.findByFileId(message.getFileId())
      .flatMap(file -> this.filesRepository.save(updateFileEntity(file, message)))
      .flatMap(file -> emailUseCase.sendEmail(message.getEmail(), "File processor", format("Error to process file: %s", file.getFileName()), null))
      .thenReturn(message);
  }

  private File updateFileEntity(File file, FileProcessorQueueMessage message) {
    file.setZipFileName(message.getZipFileName());
    file.setStatus(message.getStatus());
    return file;
  }

  private void deleteTempFile(Path destinationPath) {
    Mono.fromRunnable(() -> {
      try {
        deleteIfExists(destinationPath);
        deleteIfExists(destinationPath.getParent());
      } catch (Exception e) {
        log.error("Error deleting temporary file: {}", e.getMessage());
      }
    }).subscribe();
  }

}
