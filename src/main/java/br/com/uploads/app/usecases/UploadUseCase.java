package br.com.uploads.app.usecases;

import br.com.uploads.app.entities.File;
import br.com.uploads.app.ports.UploadQueue;
import br.com.uploads.app.repositories.FilesRepository;
import br.com.uploads.app.usecases.models.FileUploadMessage;
import br.com.uploads.app.usecases.models.UploadQueueMessage;
import br.com.uploads.enums.UploadFileStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static br.com.uploads.enums.FileStatusEnum.FAILED;
import static br.com.uploads.enums.FileStatusEnum.PROCESSING;
import static br.com.uploads.enums.UploadFileStatus.UPLOAD_FAILURE;
import static br.com.uploads.enums.UploadFileStatus.UPLOAD_SUCCESS;
import static br.com.uploads.utils.JsonUtils.toJson;
import static br.com.uploads.webui.constants.Constants.EMAIL_CONTEXT_KEY;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UploadUseCase {

  private final BucketUseCase bucketUseCase;
  private final UploadQueue messageQueue;
  private final FilesRepository filesRepository;
  private final EmailUseCase emailUseCase;

  public Mono<Void> uploadFiles(Flux<FilePart> files) {
    return Mono.deferContextual(ctx -> {
      var message = UploadQueueMessage.builder()
        .email(ctx.get(EMAIL_CONTEXT_KEY))
        .build();

      return files.flatMap(file -> bucketUseCase.uploadFile(file)
          .flatMap(f -> updateUploadQueueMessage(message, file, UPLOAD_SUCCESS))
          .onErrorResume(error -> getFileUploadMessageError(file)
            .flatMap(f -> updateUploadQueueMessage(message, file, UPLOAD_FAILURE))
            .flatMap(f -> this.sendEmailNotification(message.getEmail(), f)))
          .flatMap(f -> this.filesRepository.save(getFileEntity(message.getEmail(), f))))
        .then(Mono.defer(() -> Mono.just(message)
          .filter(m -> !m.getFiles().isEmpty())
          .flatMap(m -> this.messageQueue.sendMessage(toJson(m)))
          .then()));
    });
  }

  private Mono<FileUploadMessage> sendEmailNotification(String email, FileUploadMessage file) {
    emailUseCase.sendEmail(email, "File processor", format("Error to upload file: %s", file.getFileName()), null)
      .subscribe();

    return Mono.just(file);
  }

  private Mono<FileUploadMessage> getFileUploadMessageError(FilePart file) {
    return Mono.just(FileUploadMessage.builder()
      .fileName(file.filename())
      .status(UPLOAD_FAILURE)
      .build());
  }

  private Mono<FileUploadMessage> updateUploadQueueMessage(UploadQueueMessage message, FilePart files, UploadFileStatus status) {
    var file = FileUploadMessage.builder()
      .fileId(UUID.randomUUID().toString())
      .fileName(files.filename())
      .status(status)
      .build();
    message.getFiles().add(file);

    return Mono.just(file);
  }

  private File getFileEntity(String email, FileUploadMessage file) {
    return File.builder()
      .email(email)
      .fileId(file.getFileId())
      .fileName(file.getFileName())
      .status(UPLOAD_SUCCESS.equals(file.getStatus()) ? PROCESSING : FAILED)
      .build();
  }

}
