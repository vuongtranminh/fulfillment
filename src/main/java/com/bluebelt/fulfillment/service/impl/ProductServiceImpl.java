package com.bluebelt.fulfillment.service.impl;

import com.bluebelt.fulfillment.exception.ResourceNotFoundException;
import com.bluebelt.fulfillment.exception.UnauthorizedException;
import com.bluebelt.fulfillment.model.Category;
import com.bluebelt.fulfillment.model.Product;
import com.bluebelt.fulfillment.model.Tag;
import com.bluebelt.fulfillment.model.role.RoleName;
import com.bluebelt.fulfillment.model.user.User;
import com.bluebelt.fulfillment.payload.PagedResponse;
import com.bluebelt.fulfillment.payload.request.ProductRequest;
import com.bluebelt.fulfillment.payload.response.ApiResponse;
import com.bluebelt.fulfillment.payload.response.ProductResponse;
import com.bluebelt.fulfillment.repository.CategoryRepository;
import com.bluebelt.fulfillment.repository.ProductRepository;
import com.bluebelt.fulfillment.repository.TagRepository;
import com.bluebelt.fulfillment.repository.UserRepository;
import com.bluebelt.fulfillment.security.UserPrincipal;
import com.bluebelt.fulfillment.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bluebelt.fulfillment.utils.AppConstants.*;
import static com.bluebelt.fulfillment.utils.AppUtils.validatePageNumberAndSize;
import static com.bluebelt.fulfillment.utils.PageUtils.pageable;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productDAO;

    @Autowired
    private UserRepository userDAO;

    @Autowired
    private CategoryRepository categoryDAO;

    @Autowired
    private TagRepository tagDAO;

    @Autowired
    private ModelMapper mapper;

    @Override
    public PagedResponse<Product> getAllProducts(int page, int size, String sort, boolean desc) {
        validatePageNumberAndSize(page, size);
        Page<Product> products = productDAO.findAll(pageable(page, size, sort, desc));
        return PagedResponse.from(products);
    }

    @Override
    public PagedResponse<Product> getProductsByCreatedBy(String username, int page, int size, String sort, boolean desc) {
        validatePageNumberAndSize(page, size);
        User user = userDAO.getUserByName(username);
        Page<Product> products = productDAO.findByCreatedBy(user.getId(), pageable(page, size, sort, desc));
        return PagedResponse.from(products);
    }

    @Override
    public PagedResponse<Product> getProductsByCategory(Long id, int page, int size, String sort, boolean desc) {
        validatePageNumberAndSize(page, size);
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, ID, id));
        Page<Product> products = productDAO.findByCategory(category.getId(), pageable(page, size, sort, desc));
        return PagedResponse.from(products);
    }

    @Override
    public PagedResponse<Product> getProductsByTag(Long id, int page, int size, String sort, boolean desc) {
        validatePageNumberAndSize(page, size);

        Tag tag = tagDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException(TAG, ID, id));

        Page<Product> products = productDAO.findAllByTagsIn(Collections.singletonList(tag), pageable(page, size, sort, desc));

        return PagedResponse.from(products);
    }

    @Override
    public Product updateProduct(Long id, ProductRequest newProductRequest, UserPrincipal currentUser) {
        Product product = productDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException(PRODUCT, ID, id));
        Category category = categoryDAO.findById(newProductRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, ID, newProductRequest.getCategoryId()));
        if (product.getCreatedBy().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            product = mapper.map(newProductRequest, Product.class);
            product.setCategory(category);
            return productDAO.save(product);
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to edit this post");

        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public ApiResponse deleteProduct(Long id, UserPrincipal currentUser) {
        Product product = productDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException(PRODUCT, ID, id));
        if (product.getCreatedBy().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            productDAO.deleteById(id);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted post");
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this post");

        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest, UserPrincipal currentUser) {
        User user = userDAO.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER, ID, 1L));
        Category category = categoryDAO.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY, ID, productRequest.getCategoryId()));

        List<Tag> tags = new ArrayList<>(productRequest.getTags().size());

        for (String name : productRequest.getTags()) {
            Tag tag = tagDAO.findByName(name);
            tag = tag == null ? tagDAO.save(new Tag(name)) : tag;

            tags.add(tag);
        }

        Product product = new Product();

        product = mapper.map(productRequest, Product.class);

        product.setCategory(category);
        product.setTags(tags);
        product.setCreatedBy(user.getId());

        Product newProduct = productDAO.save(product);

        ProductResponse productResponse = new ProductResponse();

        productResponse = mapper.map(newProduct, ProductResponse.class);

        productResponse.setCategory(newProduct.getCategory().getTitle());

        List<String> tagNames = new ArrayList<>(newProduct.getTags().size());

        for (Tag tag : newProduct.getTags()) {
            tagNames.add(tag.getName());
        }

        productResponse.setTags(tagNames);

        return productResponse;
    }

    @Override
    public Product getProduct(Long id) {
        return productDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException(PRODUCT, ID, id));
    }
}
