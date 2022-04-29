package com.bluebelt.fulfillment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Meta {

    public long totalElements;

    public int totalPages;

    public int page;

    public int size;

}
