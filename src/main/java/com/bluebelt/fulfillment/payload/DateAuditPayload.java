package com.bluebelt.fulfillment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DateAuditPayload {

    private Instant createdAt;

    private Instant updatedAt;

}
