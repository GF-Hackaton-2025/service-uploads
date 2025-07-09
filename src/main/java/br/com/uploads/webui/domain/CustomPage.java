package br.com.uploads.webui.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
public class CustomPage<T> {

  private final List<T> items;
  private final int pageNumber;
  private final int pageSize;
  private final boolean hasNext;

  public CustomPage(List<T> items, int pageNumber, int pageSize, boolean hasNext) {
    this.items = items;
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.hasNext = hasNext;
  }

  public <U> CustomPage<U> map(Function<? super T, ? extends U> converter) {
    List<U> convertedItems = this.items.stream()
      .map(converter)
      .collect(Collectors.toList());

    return new CustomPage<>(convertedItems, this.pageNumber, this.pageSize, this.hasNext);
  }
}
