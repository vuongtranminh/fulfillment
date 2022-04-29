package com.bluebelt.fulfillment.controller;

import com.bluebelt.fulfillment.model.Product;
import com.bluebelt.fulfillment.payload.PagedResponse;
import com.bluebelt.fulfillment.payload.request.ProductRequest;
import com.bluebelt.fulfillment.payload.response.ApiResponse;
import com.bluebelt.fulfillment.payload.response.ProductResponse;
import com.bluebelt.fulfillment.security.CurrentUser;
import com.bluebelt.fulfillment.security.UserPrincipal;
import com.bluebelt.fulfillment.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.bluebelt.fulfillment.utils.AppConstants.*;

@Tag(name = "Product")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<PagedResponse<Product>> getAllProducts(
            @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT) String sort,
            @RequestParam(value = "desc", required = false, defaultValue = DEFAULT_DESC) Boolean desc) {
        PagedResponse<Product> response = productService.getAllProducts(page, size, sort, desc);

        return new ResponseEntity< >(response, HttpStatus.OK);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<PagedResponse<Product>> getProductsByCategory(
            @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT) String sort,
            @RequestParam(value = "desc", required = false, defaultValue = DEFAULT_DESC) Boolean desc,
            @PathVariable(name = "id") Long id) {
        PagedResponse<Product> response = productService.getProductsByCategory(id, page, size, sort, desc);

        return new ResponseEntity< >(response, HttpStatus.OK);
    }

    @GetMapping("/tag/{id}")
    public ResponseEntity<PagedResponse<Product>> getProductsByTag(
            @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT) String sort,
            @RequestParam(value = "desc", required = false, defaultValue = DEFAULT_DESC) Boolean desc,
            @PathVariable(name = "id") Long id) {
        PagedResponse<Product> response = productService.getProductsByTag(id, page, size, sort, desc);

        return new ResponseEntity< >(response, HttpStatus.OK);
    }

    @GetMapping("/addby/{id}")
    public ResponseEntity<PagedResponse<Product>> getProductsByCreatedBy(
            @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT) String sort,
            @RequestParam(value = "desc", required = false, defaultValue = DEFAULT_DESC) Boolean desc,
            @PathVariable(name = "username") String username) {
        PagedResponse<Product> response = productService.getProductsByCreatedBy(username, page, size, sort, desc);

        return new ResponseEntity< >(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest productRequest,
                                                   @CurrentUser UserPrincipal currentUser) {
        ProductResponse productResponse = productService.addProduct(productRequest, currentUser);

        return new ResponseEntity< >(productResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable(name = "id") Long id) {
        Product product = productService.getProduct(id);

        return new ResponseEntity< >(product, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable(name = "id") Long id,
                                        @Valid @RequestBody ProductRequest newProductRequest, @CurrentUser UserPrincipal currentUser) {
        Product product = productService.updateProduct(id, newProductRequest, currentUser);

        return new ResponseEntity< >(product, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = productService.deleteProduct(id, currentUser);

        return new ResponseEntity< >(apiResponse, HttpStatus.OK);
    }

}
