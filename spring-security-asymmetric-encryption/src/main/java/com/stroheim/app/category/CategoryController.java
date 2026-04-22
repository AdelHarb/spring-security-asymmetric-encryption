package com.stroheim.app.category;

import com.stroheim.app.category.request.CategoryRequest;
import com.stroheim.app.category.request.CategoryUpdateRequest;
import com.stroheim.app.category.response.CategoryResponse;
import com.stroheim.app.common.RestResponse;
import com.stroheim.app.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category API", description = "API for managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<RestResponse> createCategory(
            @RequestBody
            @Valid
            CategoryRequest request,
            Authentication authentication
            ) {

        final String userId = extractUserIdFromAuthenticationToken(authentication);
        final String catId = this.categoryService.createCategory(request, userId);
        return ResponseEntity
                .status(CREATED)
                .body(new RestResponse(catId));
    }


    @PutMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<RestResponse> updateCategory(
            @RequestBody
            @Valid
            final CategoryUpdateRequest request,
            @PathVariable("category-id")
            final String categoryId,
            final Authentication authentication
    ) {
        final String userId = extractUserIdFromAuthenticationToken(authentication);
        this.categoryService.updateCategory(request, categoryId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new RestResponse("Category updated successfully"));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllCategories(
            final Authentication authentication
    ) {
        final String userId = extractUserIdFromAuthenticationToken(authentication);
        return ResponseEntity.ok(this.categoryService.findAllByOwner(userId));
    }
    @GetMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<CategoryResponse> findCategoryById(
            @PathVariable("category-id")
            final String categoryId
    ) {
        return ResponseEntity.ok(this.categoryService.findCategoryById(categoryId));
    }

    @DeleteMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<RestResponse> deleteCategoryById(
            @PathVariable("category-id")
            final String categoryId
    ) {
        this.categoryService.deleteCategoryById(categoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new RestResponse("Category deletion initiated successfully"));
    }

    private static String extractUserIdFromAuthenticationToken(Authentication authentication) {
        final String userId = ((User) authentication.getPrincipal()).getId();
        return userId;
    }


}
