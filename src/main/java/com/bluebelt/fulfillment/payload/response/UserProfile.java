package com.bluebelt.fulfillment.payload.response;

import com.bluebelt.fulfillment.model.user.Info;
import com.bluebelt.fulfillment.model.user.Photo;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserProfile {

    private Long id;
    private String username;
    private Info info;
    private String email;
    List<Photo> photos;
    private Instant joinedAt;

}
