package com.bluebelt.fulfillment.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InfoRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private String gender;

    private String nativePlace;
    private String placeOfPermanent;
    private String avatar;
    private String identificationCardFront;

    private String identificationCardBack;

}
