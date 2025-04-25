package com.sdp.menuservice.controller;

import com.sdp.menuservice.dto.MenuCategoryDTO;
import com.sdp.menuservice.dto.MenuItemDTO;
import com.sdp.menuservice.dto.Request.MenuItemRequestDTO;
import com.sdp.menuservice.dto.Request.StockUpdateRequestDTO;
import com.sdp.menuservice.service.MenuCategoryService;
import com.sdp.menuservice.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemService menuItemService;
    private final MenuCategoryService categoryService;

    // Category endpoints
    @GetMapping("/categories")
    public ResponseEntity<List<MenuCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<MenuCategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/categories")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuCategoryDTO> createCategory(@Valid @RequestBody MenuCategoryDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.createCategory(categoryDTO), HttpStatus.CREATED);
    }

    @PutMapping("/categories/{id}")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuCategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody MenuCategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/categories/{id}")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Menu item endpoints
    @GetMapping("/items")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByCategory(categoryId));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getMenuItemById(id));
    }

    @PostMapping("/items")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuItemDTO> createMenuItem(@Valid @RequestBody MenuItemRequestDTO menuItemDTO) {
        return new ResponseEntity<>(menuItemService.createMenuItem(menuItemDTO), HttpStatus.CREATED);
    }

    @PutMapping("/items/{id}")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequestDTO menuItemDTO) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, menuItemDTO));
    }

    @DeleteMapping("/items/{id}")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/items/{id}/variants/{variantId}/stock")
//    @PreAuthorize("hasAnyRole('OWNER', 'WAITER')")
    public ResponseEntity<MenuItemDTO> updateMenuItemStock(
            @PathVariable Long id,
            @PathVariable Long variantId,
            @Valid @RequestBody StockUpdateRequestDTO stockUpdateRequest) {
        return ResponseEntity.ok(menuItemService.updateMenuItemStock(id, variantId, stockUpdateRequest));
    }

    @PutMapping("/items/{id}/availability")
//    @PreAuthorize("hasAnyRole('OWNER', 'WAITER')")
    public ResponseEntity<MenuItemDTO> updateMenuItemAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        return ResponseEntity.ok(menuItemService.updateMenuItemAvailability(id, available));
    }
}
