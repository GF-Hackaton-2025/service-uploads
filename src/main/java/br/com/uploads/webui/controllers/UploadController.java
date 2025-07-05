package br.com.uploads.webui.controllers;

import br.com.uploads.app.usecases.UploadUseCase;
import br.com.uploads.webui.domain.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
import static br.com.uploads.webui.constants.Descriptions.EMAIL;
import static br.com.uploads.webui.constants.Descriptions.FILES;
import static br.com.uploads.webui.constants.Errors.FIELD_REQUIRED;
import static br.com.uploads.webui.constants.Errors.HEADER_REQUIRED;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/upload")
@Tag(name = "Upload", description = "Controller for managing file uploads")
@ApiResponse(responseCode = "401", description = "Unauthorized",
  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "422", description = "Unprocessable Entity",
  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "400", description = "Bad Request",
  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error",
  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
public class UploadController {

  private static final Logger log = LoggerFactory.getLogger(UploadController.class);

  private final UploadUseCase uploadUseCase;

  @PostMapping
  @Operation(summary = "Upload files", description = "Endpoint to upload files")
  public Mono<Void> uploadFiles(
    @Parameter(description = EMAIL)
    @RequestHeader(required = false)
    @NotBlank(message = HEADER_REQUIRED) final String email,
    @Parameter(description = FILES)
    @NotNull(message = FIELD_REQUIRED)
    @RequestPart(value = "files", required = false) final Flux<FilePart> files) {

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
