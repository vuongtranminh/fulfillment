package com.bluebelt.fulfillment.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * tóm tắt người dùng
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserSummary {

    private Long id;
    private String username;
    private String avatar; // return avatar url

}
