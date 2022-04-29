package com.bluebelt.fulfillment.repository;

import com.bluebelt.fulfillment.model.Product;
import com.bluebelt.fulfillment.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCreatedBy(Long userId, Pageable pageable);

    Page<Product> findByCategory(Long categoryId, Pageable pageable);

    Page<Product> findAllByTagsIn(List<Tag> tags, Pageable pageable);

    Long countByCreatedBy(Long userId);

}
