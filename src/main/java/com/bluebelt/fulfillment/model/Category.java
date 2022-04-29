package com.bluebelt.fulfillment.model;

import com.bluebelt.fulfillment.model.audit.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "categories")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
/**
 * @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
 * Khi sử dụng có liên kết sẽ không bị lặp truy vấn. chỉ hiển thị ra id. chống đệ quy vô tận
 */
public class Category extends UserDateAudit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    public List<Product> getProducts() {
        return this.products == null ? null : new ArrayList<>(this.products);
    }

    public void setProducts(List<Product> products) {
        // trả về một list không thể sửa đổi
        this.products = products == null ? null : Collections.unmodifiableList(products);
    }

    public Category(String title) {
        super();
        this.title = title;
    }

}
