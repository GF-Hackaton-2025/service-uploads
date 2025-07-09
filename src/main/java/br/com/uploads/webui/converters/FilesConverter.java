package br.com.uploads.webui.converters;

import br.com.uploads.app.entities.File;
import br.com.uploads.utils.Dates;
import br.com.uploads.webui.domain.filescontroller.response.GetFileResponse;

public class FilesConverter {

  public static GetFileResponse convertToGetFileResponse(File file) {
    return GetFileResponse.builder()
      .id(String.valueOf(file.getId()))
      .email(file.getEmail())
      .fileId(file.getFileId())
      .fileName(file.getFileName())
      .status(file.getStatus() != null ? file.getStatus().name() : null)
      .uploadDate(Dates.format(file.getUploadDate()))
      .build();
  }
}
