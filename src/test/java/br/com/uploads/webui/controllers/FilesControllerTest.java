package br.com.uploads.webui.controllers;

import br.com.uploads.app.entities.File;
import br.com.uploads.app.usecases.FileUseCase;
import br.com.uploads.webui.domain.CustomPage;
import br.com.uploads.webui.domain.PaginationResponse;
import br.com.uploads.webui.domain.filescontroller.response.GetFileResponse;
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
class FilesControllerTest {

  @InjectMocks
  private FilesController filesController;
  @Mock
  private FileUseCase fileUseCase;

  @Test
  void getFileReturnsPaginationResponseWhenValidParametersProvided() {
    String email = "user@example.com";
    int page = 1;
    int limit = 25;
    String sort = "DESC";

    CustomPage<File> mockPage = new CustomPage<>(List.of(new File(), new File()), page, limit, false);
    when(fileUseCase.getFileByUser(email, page, limit, sort))
      .thenReturn(Mono.just(mockPage));

    Mono<PaginationResponse<GetFileResponse>> result = filesController.getFile(email, page, limit, sort);

    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(2, response.getItems().size());
        assertEquals(page, response.getPageNumber());
        assertFalse(response.getHasNext());
      })
      .verifyComplete();
  }

}