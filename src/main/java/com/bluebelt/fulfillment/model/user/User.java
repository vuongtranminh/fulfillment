package com.bluebelt.fulfillment.model.user;

import com.bluebelt.fulfillment.model.audit.DateAudit;
import com.bluebelt.fulfillment.model.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
        @UniqueConstraint(columnNames = { "email" }) })
public class User extends DateAudit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 15)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 100)
    private String password;

    @NotBlank
    @NaturalId
    @Size(max = 40)
    @Email
    private String email;


    /**
     * fetch = FetchType.LAZY tức là khi bạn find, select đối tượng Company từ database thì nó sẽ không lấy các đối tượng Employee liên quan
     * fetch = FetchType.EAGER tức là khi bạn find, select đối tượng Company từ database thì tất cả các đối tượng Employee liên quan sẽ được lấy ra và lưu vào listEmployee
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * cascade = CascadeType.ALL xóa phần tử cha thì sẽ xóa hết phần tử con liên kết
     * orphanRemoval = true: Thực thể "con" bị xóa khi không còn tham chiếu (cha mẹ của nó có thể không bị xóa).
     * - CascadeType.REMOVE: Thực thể "Con" chỉ bị xóa khi "Cha mẹ" của nó bị xóa.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "info_id")
    private Info info;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public List<Photo> getPhotos() {
        return photos == null ? null : new ArrayList<>(photos);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos == null ? null : Collections.unmodifiableList(photos);
    }
}
