package br.com.uploads.webui.controllers;

import br.com.uploads.app.usecases.UploadUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static br.com.uploads.webui.constants.Constants.EMAIL_CONTEXT_KEY;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Upload", description = "Controller for managing file uploads")
@RequestMapping(value = "/api/v1/upload")
public class UploadController {

  private final UploadUseCase uploadUseCase;

  private static final Logger log = LoggerFactory.getLogger(UploadController.class);

  @Operation(summary = "Upload files", description = "Endpoint to upload files")
  @PostMapping
  public Mono<Void> uploadFiles(
    @RequestHeader
    String email,
    @RequestPart("files")
    Flux<FilePart> files) {
    return files
      .collectList()
      .flatMap(fileList -> {
        uploadUseCase.uploadFiles(Flux.fromIterable(fileList))
          .contextWrite(context -> context.put(EMAIL_CONTEXT_KEY, email))
          .doOnSuccess(aVoid -> log.info("Upload completed for user: {}", email))
          .doOnError(error -> log.error("Error during upload for user: {}", email, error))
          .subscribe();

        return Mono.empty();
      });
  }

}
