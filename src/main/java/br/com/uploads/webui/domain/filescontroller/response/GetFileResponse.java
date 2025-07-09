package br.com.uploads.webui.domain.filescontroller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static br.com.uploads.webui.constants.Descriptions.EMAIL;
import static br.com.uploads.webui.constants.Descriptions.FILE_ID;
import static br.com.uploads.webui.constants.Descriptions.FILE_NAME;
import static br.com.uploads.webui.constants.Descriptions.ID;
import static br.com.uploads.webui.constants.Descriptions.STATUS;
import static br.com.uploads.webui.constants.Descriptions.UPLOAD_DATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetFileResponse {

  @Schema(description = ID)
  private String id;
  @Schema(description = EMAIL)
  private String email;
  @Schema(description = FILE_ID)
  private String fileId;
  @Schema(description = FILE_NAME)
  private String fileName;
  @Schema(description = STATUS)
  private String status;
  @Schema(description = UPLOAD_DATE)
  private String uploadDate;

}
