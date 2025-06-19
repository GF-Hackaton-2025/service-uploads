package br.com.uploads.app.usecases.models;

import br.com.uploads.enums.UploadFileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadMessage {

  private String fileName;
  private UploadFileStatus status;

}
