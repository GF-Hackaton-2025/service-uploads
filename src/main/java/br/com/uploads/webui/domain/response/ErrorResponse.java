package br.com.uploads.webui.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static br.com.uploads.webui.constants.Descriptions.FIELDS;
import static br.com.uploads.webui.constants.Descriptions.HTTP_CODE;
import static br.com.uploads.webui.constants.Descriptions.HTTP_DESCRIPTION;
import static br.com.uploads.webui.constants.Descriptions.MESSAGE;
import static br.com.uploads.webui.constants.Descriptions.PATH;
import static br.com.uploads.webui.constants.Descriptions.TIMESTAMP;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  @Schema(description = HTTP_CODE)
  private int httpCode;
  @Schema(description = HTTP_DESCRIPTION)
  private String httpDescription;
  @Schema(description = MESSAGE)
  private String message;
  @Schema(description = PATH)
  private String path;
  @Schema(description = TIMESTAMP)
  private String timestamp;
  @Schema(description = FIELDS)
  private List<ErrorField> errors;

}
