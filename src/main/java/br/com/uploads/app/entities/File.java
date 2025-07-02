package br.com.uploads.app.entities;

import br.com.uploads.enums.FileStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "files")
public class File {

  @Id
  private ObjectId id;
  private String email;
  private String fileId;
  private String fileName;
  private FileStatusEnum status;
  private String zipFileName;

  @Builder.Default
  private LocalDateTime uploadDate = LocalDateTime.now();

}
