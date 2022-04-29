package com.bluebelt.fulfillment.model.user;

import com.bluebelt.fulfillment.model.audit.UserDateAudit;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
public class Address extends UserDateAudit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // quê quán
    private String nativePlace;

    // địa chỉ thường chú
    private String placeOfPermanent;

    // phát triển thêm city, district, commune

    @OneToOne(mappedBy = "address")
    private User user;
}
