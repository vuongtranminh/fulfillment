package com.bluebelt.fulfillment.service.impl;

import com.bluebelt.fulfillment.exception.ResourceNotFoundException;
import com.bluebelt.fulfillment.exception.UnauthorizedException;
import com.bluebelt.fulfillment.model.Category;
import com.bluebelt.fulfillment.model.role.RoleName;
import com.bluebelt.fulfillment.payload.PagedResponse;
import com.bluebelt.fulfillment.payload.response.ApiResponse;
import com.bluebelt.fulfillment.repository.CategoryRepository;
import com.bluebelt.fulfillment.security.UserPrincipal;
import com.bluebelt.fulfillment.service.CategoryService;
import com.bluebelt.fulfillment.utils.AppUtils;
import com.bluebelt.fulfillment.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryDAO;

	@Override
	public PagedResponse<Category> getAllCategories(int page, int size, String sort, boolean desc) {
		AppUtils.validatePageNumberAndSize(page, size);
		Page<Category> categories = categoryDAO.findAll(PageUtils.pageable(page, size, sort, desc));
		return PagedResponse.from(categories);
	}

	@Override
	public ResponseEntity<Category> getCategory(Long id) {
		Category category = categoryDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
		return new ResponseEntity<>(category, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Category> addCategory(Category category, UserPrincipal currentUser) {
		Category newCategory = categoryDAO.save(category);
		return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Category> updateCategory(Long id, Category newCategory, UserPrincipal currentUser) {
		Category category = categoryDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
		if (category.getCreatedBy().equals(currentUser.getId()) || currentUser.getAuthorities()
				.contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			category.setTitle(newCategory.getTitle());
			Category updatedCategory = categoryDAO.save(category);
			return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
		}

		throw new UnauthorizedException("You don't have permission to edit this category");
	}

	@Override
	public ResponseEntity<ApiResponse> deleteCategory(Long id, UserPrincipal currentUser) {
		Category category = categoryDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("category", "id", id));
		if (category.getCreatedBy().equals(currentUser.getId()) || currentUser.getAuthorities()
				.contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			categoryDAO.deleteById(id);
			return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "You successfully deleted category"), HttpStatus.OK);
		}
		throw new UnauthorizedException("You don't have permission to delete this category");
	}
}






















