package com.sdp.menuservice.service.impl;

import com.sdp.menuservice.dto.MenuCategoryDTO;
import com.sdp.menuservice.model.MenuCategory;
import com.sdp.menuservice.repository.MenuCategoryRepository;
import com.sdp.menuservice.service.MenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private final MenuCategoryRepository categoryRepository;

    @Override
    public List<MenuCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MenuCategoryDTO getCategoryById(Long id) {
        MenuCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToDTO(category);
    }

    @Override
    @Transactional
    public MenuCategoryDTO createCategory(MenuCategoryDTO categoryDTO) {
        MenuCategory category = new MenuCategory();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        MenuCategory savedCategory = categoryRepository.save(category);
        return mapToDTO(savedCategory);
    }

    @Override
    @Transactional
    public MenuCategoryDTO updateCategory(Long id, MenuCategoryDTO categoryDTO) {
        MenuCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        MenuCategory updatedCategory = categoryRepository.save(category);
        return mapToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private MenuCategoryDTO mapToDTO(MenuCategory category) {
        MenuCategoryDTO dto = new MenuCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
