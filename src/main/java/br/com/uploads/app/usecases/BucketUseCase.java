package br.com.uploads.app.usecases;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static br.com.uploads.webui.constants.Constants.EMAIL_CONTEXT_KEY;
import static br.com.uploads.webui.constants.Constants.UPLOADS_BUCKET_NAME;

@Service
@RequiredArgsConstructor
public class BucketUseCase {

  private final S3Client s3Client;

  private static final Logger log = LoggerFactory.getLogger(BucketUseCase.class);

  public Mono<FilePart> uploadFile(FilePart file) {
    return Mono.deferContextual(ctx -> {
      String email = ctx.get(EMAIL_CONTEXT_KEY);
      return file.content()
        .map(dataBuffer -> {
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          try {
            ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            outputStream.write(bytes);
            return outputStream.toByteArray();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .reduce((a, b) -> {
          byte[] combined = new byte[a.length + b.length];
          System.arraycopy(a, 0, combined, 0, a.length);
          System.arraycopy(b, 0, combined, a.length, b.length);
          return combined;
        })
        .flatMap(bytes -> Mono.just(s3Client.putObject(PutObjectRequest.builder()
            .bucket(UPLOADS_BUCKET_NAME)
            .key(email + "/" + file.filename())
            .contentType(String.valueOf(file.headers().getContentType()))
            .build(), RequestBody.fromBytes(bytes)))
          .flatMap(response -> Mono.just(file))
          .doOnSuccess(aVoid -> log.info(String.format("Uploaded file %s for user: %s", file.filename(), email)))
          .doOnError(error -> log.error(String.format("Error to upload file %s for user: %s", file.filename(), email), error)));
    });
  }

}
