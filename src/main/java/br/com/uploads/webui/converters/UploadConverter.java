package br.com.uploads.webui.converters;

import br.com.uploads.app.usecases.models.FileUploadMessage;
import br.com.uploads.app.usecases.models.UploadQueueMessage;
import br.com.uploads.webui.domain.response.FileUploadResponse;
import br.com.uploads.webui.domain.response.UploadFilesResponse;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UploadConverter {

  public static UploadFilesResponse toUploadFilesResponse(UploadQueueMessage message) {
    return UploadFilesResponse.builder()
      .email(message.getEmail())
      .files(message.getFiles().stream().map(UploadConverter::toFileUploadResponse).toList())
      .build();
  }

  private static FileUploadResponse toFileUploadResponse(FileUploadMessage file) {
    return FileUploadResponse.builder()
      .fileId(file.getFileId())
      .fileName(file.getFileName())
      .status(file.getStatus())
      .build();
  }

}
