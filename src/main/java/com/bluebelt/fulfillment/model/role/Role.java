package com.bluebelt.fulfillment.model.role;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    /**
     * @NaturalId tạo khoá tự nhiên. có thể nạp thực thể bằng trường này thay cho Id
     */
    private RoleName name;

    public Role(RoleName name) {
        this.name = name;
    }
}
