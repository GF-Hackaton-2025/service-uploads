package br.com.uploads.app.usecases;

import br.com.uploads.app.usecases.models.FileUploadMessage;
import br.com.uploads.app.usecases.models.UploadQueueMessage;
import br.com.uploads.enums.UploadFileStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static br.com.uploads.enums.UploadFileStatus.UPLOAD_FAILURE;
import static br.com.uploads.enums.UploadFileStatus.UPLOAD_SUCCESS;
import static br.com.uploads.utils.JsonUtils.toJson;
import static br.com.uploads.webui.constants.Constants.EMAIL_CONTEXT_KEY;

@Service
@RequiredArgsConstructor
public class UploadUseCase {

  private final BucketUseCase bucketUseCase;
  private final QueueUseCase queueUseCase;

  public Mono<Void> uploadFiles(Flux<FilePart> files) {
    return Mono.deferContextual(ctx -> {
      var message = UploadQueueMessage.builder()
        .email(ctx.get(EMAIL_CONTEXT_KEY))
        .build();

      return files.flatMap(file -> bucketUseCase.uploadFile(file)
          .flatMap(f -> updateUploadQueueMessage(message, file, UPLOAD_SUCCESS))
          .onErrorResume(error -> updateUploadQueueMessage(message, file, UPLOAD_FAILURE)))
        .collectList()
        .flatMap(updatedMessage -> queueUseCase.sendMessage(toJson(updatedMessage)))
        .then();
    });
  }

  private Mono<UploadQueueMessage> updateUploadQueueMessage(UploadQueueMessage message, FilePart files, UploadFileStatus status) {
    message.getFiles().add(FileUploadMessage.builder()
      .fileName(files.filename())
      .status(status)
      .build());

    return Mono.just(message);
  }

}
