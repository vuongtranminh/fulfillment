package com.bluebelt.fulfillment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PagedResponse<T> {

    public Meta meta;

    public List<T> content;

    public static <T> PagedResponse<T> from(Page<T> page) {
        List<T> content = page.getNumberOfElements() == 0 ? Collections.emptyList() : page.getContent();
        return new PagedResponse<T>()
                .withMeta(new Meta().builder()
                        .page(page.getNumber() + 1)
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages()).build())
                .withContent(content);
    }

    public List<T> getContent() {
        return content == null ? null : new ArrayList<>(content);
    }

    public final void setContent(List<T> content) {
        if (content == null) {
            this.content = null;
        } else {
            this.content = Collections.unmodifiableList(content);
        }
    }

}
