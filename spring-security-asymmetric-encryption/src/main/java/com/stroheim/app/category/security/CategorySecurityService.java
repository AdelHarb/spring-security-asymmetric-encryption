package com.stroheim.app.category.security;

import com.stroheim.app.category.Category;
import com.stroheim.app.category.CategoryRepository;
import com.stroheim.app.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategorySecurityService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public boolean isCategoryOwner(final String categoryId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String userId = ((User) authentication.getPrincipal()).getId();

        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        return category.getCreatedBy()
                .equals(userId);
    }
}
