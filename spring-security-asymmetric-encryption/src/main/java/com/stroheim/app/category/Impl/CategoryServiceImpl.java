package com.stroheim.app.category.Impl;

import com.stroheim.app.category.Category;
import com.stroheim.app.category.CategoryRepository;
import com.stroheim.app.category.CategoryService;
import com.stroheim.app.category.request.CategoryRequest;
import com.stroheim.app.category.request.CategoryUpdateRequest;
import com.stroheim.app.category.response.CategoryResponse;
import com.stroheim.app.exception.BusinessException;
import com.stroheim.app.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public String createCategory(CategoryRequest request, String userId) {

        checkCategoryUnicityForUser(request.getName(), userId);
        final Category category = this.categoryMapper.toCategory(request);
        return this.categoryRepository.save(category).getId();
    }


    @Override
    public void updateCategory(CategoryUpdateRequest request, String catId , String userId) {

        final Category categoryToUpdate = this.categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + catId));
        checkCategoryUnicityForUser(request.getName(), userId);

        this.categoryMapper.mergeCategory(categoryToUpdate, request);
        this.categoryRepository.save(categoryToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllByOwner(String userId) {

        return this.categoryRepository
                .findAllByUserId(userId)
                .stream()
                .map(this.categoryMapper::toCategoryResponse)
                .toList();
    }

    @Override
    public CategoryResponse findCategoryById(String catId) {

        return this.categoryRepository.findById(catId)
                .map(this.categoryMapper::toCategoryResponse)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + catId));
    }

    @Override
    public void deleteCategoryById(String catId) {

        // TODO
        // mark the c ategory for deletion
        // the scheduler should pick up all the marked categories and perform the deletion
    }

    private void checkCategoryUnicityForUser(String name, String userId) {
        final boolean alreadyExistsForUser = this.categoryRepository.findByNameAndUser(userId, name);
        if (alreadyExistsForUser)
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER);
    }
}
