package br.com.uploads.webui.controllers;

import br.com.uploads.app.usecases.FileUseCase;
import br.com.uploads.webui.constants.Descriptions;
import br.com.uploads.webui.converters.FilesConverter;
import br.com.uploads.webui.domain.ErrorResponse;
import br.com.uploads.webui.domain.PaginationResponse;
import br.com.uploads.webui.domain.filescontroller.response.GetFileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

import static br.com.uploads.webui.constants.Descriptions.EMAIL;
import static br.com.uploads.webui.constants.Descriptions.EXAMPLE_ASC_DESC;
import static br.com.uploads.webui.constants.Descriptions.SORT;
import static br.com.uploads.webui.constants.Descriptions.TOTAL_PAGE;
import static br.com.uploads.webui.constants.Errors.HEADER_REQUIRED;
import static br.com.uploads.webui.constants.Errors.LIMIT_MAX;
import static br.com.uploads.webui.constants.Errors.PAGE_MIN;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/file")
@Tag(name = "Upload", description = "Controller for searching file uploads")
@ApiResponse(responseCode = "401", description = "Unauthorized",
  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "422", description = "Unprocessable Entity",
  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "400", description = "Bad Request",
  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error",
  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
public class FilesController {

  private static final Logger log = LoggerFactory.getLogger(FilesController.class);

  private final FileUseCase fileUseCase;

  @GetMapping
  @Operation(summary = "Get files by user", description = "Endpoint to get files by user email")
  public Mono<PaginationResponse<GetFileResponse>> getFile(
    @Parameter(description = EMAIL, required = false)
    @RequestParam(required = false)
    @Valid @NotBlank(message = HEADER_REQUIRED)
    final String email,
    @Parameter(description = TOTAL_PAGE)
    @RequestParam(required = false, defaultValue = "1")
    @Valid @Min(value = 1, message = PAGE_MIN)
    final Integer page,
    @Parameter(description = Descriptions.LIMIT)
    @RequestParam(required = false, defaultValue = "25")
    @Valid @Max(value = 50, message = LIMIT_MAX)
    final Integer limit,
    @Parameter(description = SORT, example = EXAMPLE_ASC_DESC)
    @RequestParam(required = false, defaultValue = "DESC")
    final String sort) {

    return fileUseCase.getFileByUser(email, page, limit, sort)
      .map(pageFile -> pageFile.map(FilesConverter::convertToGetFileResponse))
      .flatMap(pageUserResponse -> Mono.just(new PaginationResponse<GetFileResponse>().convertToResponse(pageUserResponse)))
      .contextWrite(Context.of("request_id", UUID.randomUUID().toString()));
  }

}
