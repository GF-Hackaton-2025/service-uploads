package br.com.uploads.webui.domain.response;

import br.com.uploads.enums.UploadFileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

  private String fileId;
  private String fileName;
  private UploadFileStatus status;

}
