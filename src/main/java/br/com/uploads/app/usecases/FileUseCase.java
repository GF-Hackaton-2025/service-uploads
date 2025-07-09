package br.com.uploads.app.usecases;

import br.com.uploads.app.entities.File;
import br.com.uploads.app.repositories.FilesRepository;
import br.com.uploads.webui.domain.CustomPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUseCase {

  private final FilesRepository filesRepository;

  public Mono<CustomPage<File>> getFileByUser(String email, Integer page, Integer limit, String sort) {
    return filesRepository.findAllUserByPagination(email, page, limit, sort)
      .doOnError(e -> log.error("Error in process getFilesByUser", e))
      .onErrorResume(Mono::error);
  }

}
