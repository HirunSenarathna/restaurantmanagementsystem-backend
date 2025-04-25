package com.sdp.menuservice.service;

import com.sdp.menuservice.dto.MenuCategoryDTO;

import java.util.List;

public interface MenuCategoryService {
    List<MenuCategoryDTO> getAllCategories();
    MenuCategoryDTO getCategoryById(Long id);
    MenuCategoryDTO createCategory(MenuCategoryDTO categoryDTO);
    MenuCategoryDTO updateCategory(Long id, MenuCategoryDTO categoryDTO);
    void deleteCategory(Long id);
}
