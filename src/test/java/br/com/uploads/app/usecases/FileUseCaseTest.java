package br.com.uploads.app.usecases;

import br.com.uploads.app.entities.File;
import br.com.uploads.app.repositories.FilesRepository;
import br.com.uploads.webui.domain.CustomPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileUseCaseTest {

  @InjectMocks
  private FileUseCase fileUseCase;
  @Mock
  private FilesRepository filesRepository;

  @Test
  void getFileByUserReturnsCustomPageWhenEmailIsValid() {
    String email = "user@example.com";
    int page = 1;
    int limit = 10;
    String sort = "uploadDate";

    CustomPage<File> mockPage = new CustomPage<>(List.of(new File(), new File()), page, limit, false);
    when(filesRepository.findAllUserByPagination(email, page, limit, sort)).thenReturn(Mono.just(mockPage));

    Mono<CustomPage<File>> result = fileUseCase.getFileByUser(email, page, limit, sort);

    StepVerifier.create(result)
      .assertNext(customPage -> {
        assertEquals(2, customPage.getItems().size());
        assertEquals(page, customPage.getPageNumber());
        assertFalse(customPage.isHasNext());
      })
      .verifyComplete();
  }

  @Test
  void getFileByUserReturnsEmptyPageWhenEmailIsEmpty() {
    String email = "";
    int page = 1;
    int limit = 10;
    String sort = "uploadDate";

    CustomPage<File> mockPage = new CustomPage<>(List.of(), page,  limit, false);
    when(filesRepository.findAllUserByPagination(email, page, limit, sort)).thenReturn(Mono.just(mockPage));

    Mono<CustomPage<File>> result = fileUseCase.getFileByUser(email, page, limit, sort);

    StepVerifier.create(result)
      .assertNext(customPage -> {
        assertEquals(0, customPage.getItems().size());
        assertEquals(page, customPage.getPageNumber());
        assertFalse(customPage.isHasNext());
      })
      .verifyComplete();
  }

  @Test
  void getFileByUserHandlesErrorGracefullyWhenRepositoryThrowsException() {
    String email = "user@example.com";
    int page = 1;
    int limit = 10;
    String sort = "uploadDate";

    when(filesRepository.findAllUserByPagination(email, page, limit, sort))
      .thenReturn(Mono.error(new RuntimeException("Database error")));

    Mono<CustomPage<File>> result = fileUseCase.getFileByUser(email, page, limit, sort);

    StepVerifier.create(result)
      .expectError(RuntimeException.class)
      .verify();
  }
}