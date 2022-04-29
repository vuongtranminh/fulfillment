package com.bluebelt.fulfillment.model.user;

import com.bluebelt.fulfillment.model.audit.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "photo", uniqueConstraints = { @UniqueConstraint(columnNames = { "type" }) })
public class Photo extends UserDateAudit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private PhotoType type;

    @NotBlank
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Photo(@NotBlank PhotoType type, @NotBlank String url) {
        this.type = type;
        this.url = url;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }
}
