package br.com.uploads.webui.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static br.com.uploads.webui.constants.Descriptions.HAS_NEXT;
import static br.com.uploads.webui.constants.Descriptions.HAS_PREVIOUS;
import static br.com.uploads.webui.constants.Descriptions.ITEMS;
import static br.com.uploads.webui.constants.Descriptions.PAGE_NUMBER;
import static br.com.uploads.webui.constants.Descriptions.PAGE_SIZE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PaginationResponse<T> {

    @Schema(description = HAS_NEXT)
    private Boolean hasNext;
    @Schema(description = HAS_PREVIOUS)
    private Boolean hasPrevious;
    @Schema(description = PAGE_NUMBER)
    private Integer pageNumber;
    @Schema(description = PAGE_SIZE)
    private Integer pageSize;
    @Schema(description = ITEMS)
    private List<T> items;

    public PaginationResponse<T> convertToResponse(CustomPage<T> lista) {
        this.hasNext = lista.isHasNext();
        this.hasPrevious = lista.getPageNumber() > 1;
        this.pageNumber = lista.getPageNumber();
        this.pageSize = lista.getPageSize();
        this.items = lista.getItems();
        return this;
    }
}
