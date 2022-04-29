package com.bluebelt.fulfillment.service;

import com.bluebelt.fulfillment.model.Product;
import com.bluebelt.fulfillment.payload.PagedResponse;
import com.bluebelt.fulfillment.payload.request.ProductRequest;
import com.bluebelt.fulfillment.payload.response.ApiResponse;
import com.bluebelt.fulfillment.payload.response.ProductResponse;
import com.bluebelt.fulfillment.security.UserPrincipal;

public interface ProductService {

    PagedResponse<Product> getAllProducts(int page, int size, String sort, boolean desc);

    PagedResponse<Product> getProductsByCreatedBy(String username, int page, int size, String sort, boolean desc);

    PagedResponse<Product> getProductsByCategory(Long id, int page, int size, String sort, boolean desc);

    PagedResponse<Product> getProductsByTag(Long id, int page, int size, String sort, boolean desc);

    Product updateProduct(Long id, ProductRequest newProductRequest, UserPrincipal currentUser);

    ApiResponse deleteProduct(Long id, UserPrincipal currentUser);

    ProductResponse addProduct(ProductRequest productRequest, UserPrincipal currentUser);

    Product getProduct(Long id);

}
