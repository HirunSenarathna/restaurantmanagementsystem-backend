package com.sdp.menuservice.service.impl;

import com.sdp.menuservice.dto.MenuItemDTO;
import com.sdp.menuservice.dto.Request.MenuItemRequestDTO;
import com.sdp.menuservice.dto.Request.StockUpdateRequestDTO;
import com.sdp.menuservice.model.MenuCategory;
import com.sdp.menuservice.model.MenuItem;
import com.sdp.menuservice.model.MenuItemVariant;
import com.sdp.menuservice.model.ItemSize;
import com.sdp.menuservice.repository.MenuCategoryRepository;
import com.sdp.menuservice.repository.MenuItemRepository;
import com.sdp.menuservice.repository.MenuItemVariantRepository;
import com.sdp.menuservice.service.MenuItemService;
import com.sdp.menuservice.service.kafka.MenuEventProducer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository categoryRepository;
    private final MenuItemVariantRepository variantRepository;
    private final MenuEventProducer menuEventProducer;

    @Override
    public List<MenuItemDTO> getAllMenuItems() {
        List<MenuItem> items = menuItemRepository.findAll();
        return items.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemDTO> getMenuItemsByCategory(Long categoryId) {
        MenuCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        List<MenuItem> items = menuItemRepository.findByCategory(category);
        return items.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        return mapToDTO(item);
    }

    @Override
    @Transactional
    public MenuItemDTO createMenuItem(MenuItemRequestDTO menuItemDTO) {
        MenuCategory category = categoryRepository.findById(menuItemDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + menuItemDTO.getCategoryId()));

        MenuItem menuItem = new MenuItem();
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setCategory(category);
        menuItem.setAvailable(true);

        MenuItem savedItem = menuItemRepository.save(menuItem);

        if (menuItemDTO.getVariants() != null && !menuItemDTO.getVariants().isEmpty()) {
            for (MenuItemRequestDTO.VariantRequest variantRequest : menuItemDTO.getVariants()) {
                MenuItemVariant variant = new MenuItemVariant();
                variant.setMenuItem(savedItem);
                variant.setSize(variantRequest.getSize());
                variant.setVariant(variantRequest.getVariant());
                variant.setPrice(variantRequest.getPrice());
                variant.setStockQuantity(variantRequest.getStockQuantity());
                variant.setAvailable(variantRequest.isAvailable());

                variantRepository.save(variant);
            }
        }

        // Publish event
        menuEventProducer.publishMenuItemCreatedEvent(savedItem.getId());

        return mapToDTO(savedItem);
    }

    @Override
    @Transactional
    public MenuItemDTO updateMenuItem(Long id, MenuItemRequestDTO menuItemDTO) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        MenuCategory category = categoryRepository.findById(menuItemDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + menuItemDTO.getCategoryId()));

        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setCategory(category);

        MenuItem updatedItem = menuItemRepository.save(menuItem);

        // Update variants
        if (menuItemDTO.getVariants() != null && !menuItemDTO.getVariants().isEmpty()) {
            for (MenuItemRequestDTO.VariantRequest variantRequest : menuItemDTO.getVariants()) {
                ItemSize size = variantRequest.getSize();

                MenuItemVariant variant = variantRepository
                        .findByMenuItemAndSizeAndVariant(updatedItem, size, variantRequest.getVariant())
                        .orElse(new MenuItemVariant());

                variant.setMenuItem(updatedItem);
                variant.setSize(size);
                variant.setVariant(variantRequest.getVariant());
                variant.setPrice(variantRequest.getPrice());
                variant.setStockQuantity(variantRequest.getStockQuantity());
                variant.setAvailable(variantRequest.isAvailable());

                variantRepository.save(variant);
            }
        }

        // Publish event
        menuEventProducer.publishMenuItemUpdatedEvent(updatedItem.getId());

        return mapToDTO(updatedItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found with id: " + id);
        }

        // Delete related variants first
        MenuItem menuItem = menuItemRepository.findById(id).get();
        List<MenuItemVariant> variants = variantRepository.findByMenuItem(menuItem);
        variantRepository.deleteAll(variants);

        // Then delete the menu item
        menuItemRepository.deleteById(id);

        // Publish event
        menuEventProducer.publishMenuItemDeletedEvent(id);
    }

    @Override
    @Transactional
    public MenuItemDTO updateMenuItemStock(Long id, Long variantId, StockUpdateRequestDTO stockUpdateRequest) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        MenuItemVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item variant not found with id: " + variantId));

        if (!variant.getMenuItem().getId().equals(menuItem.getId())) {
            throw new IllegalArgumentException("Variant does not belong to this menu item");
        }

        variant.setStockQuantity(stockUpdateRequest.getStockQuantity());
        variant.setAvailable(stockUpdateRequest.isAvailable());

        variantRepository.save(variant);

        // Publish event
        menuEventProducer.publishMenuItemStockUpdatedEvent(menuItem.getId(), variantId);

        return mapToDTO(menuItem);
    }

    @Override
    @Transactional
    public MenuItemDTO updateMenuItemAvailability(Long id, boolean available) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        menuItem.setAvailable(available);
        MenuItem updatedItem = menuItemRepository.save(menuItem);

        // Publish event
        menuEventProducer.publishMenuItemAvailabilityUpdatedEvent(updatedItem.getId());

        return mapToDTO(updatedItem);
    }

    private MenuItemDTO mapToDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setCategoryId(menuItem.getCategory().getId());
        dto.setCategoryName(menuItem.getCategory().getName());
        dto.setAvailable(menuItem.isAvailable());

        List<MenuItemVariant> variants = variantRepository.findByMenuItem(menuItem);
        List<MenuItemDTO.MenuItemVariantDTO> variantDTOs = new ArrayList<>();

        for (MenuItemVariant variant : variants) {
            MenuItemDTO.MenuItemVariantDTO variantDTO = new MenuItemDTO.MenuItemVariantDTO();
            variantDTO.setId(variant.getId());
            variantDTO.setSize(variant.getSize().name());
            variantDTO.setVariant(variant.getVariant());
            variantDTO.setPrice(variant.getPrice().doubleValue());
            variantDTO.setStockQuantity(variant.getStockQuantity());
            variantDTO.setAvailable(variant.isAvailable());

            variantDTOs.add(variantDTO);
        }

        dto.setVariants(variantDTOs);

        return dto;
    }
}
