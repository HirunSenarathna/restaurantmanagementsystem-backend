package com.sdp.menuservice.service.impl;

import com.sdp.menuservice.dto.MenuItemDTO;
import com.sdp.menuservice.dto.MenuItemVariantResponseDTO;
import com.sdp.menuservice.dto.MenuItemWithVariantResponseDTO;
import com.sdp.menuservice.dto.Request.MenuItemRequest;
import com.sdp.menuservice.dto.Request.MenuItemRequestDTO;
import com.sdp.menuservice.dto.Request.MenuItemVariantRequest;
import com.sdp.menuservice.dto.Request.StockUpdateRequestDTO;
import com.sdp.menuservice.model.MenuCategory;
import com.sdp.menuservice.model.MenuItem;
import com.sdp.menuservice.model.MenuItemVariant;
import com.sdp.menuservice.model.ItemSize;
import com.sdp.menuservice.repository.MenuCategoryRepository;
import com.sdp.menuservice.repository.MenuItemRepository;
import com.sdp.menuservice.repository.MenuItemVariantRepository;
import com.sdp.menuservice.service.CloudinaryService;
import com.sdp.menuservice.service.MenuItemService;
import com.sdp.menuservice.service.kafka.MenuEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository categoryRepository;
    private final MenuItemVariantRepository variantRepository;
    private final MenuEventProducer menuEventProducer;

    private final CloudinaryService cloudinaryService;

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

        // Handle base64 image upload if provided
        if (menuItemDTO.getImageBase64() != null && !menuItemDTO.getImageBase64().isEmpty()) {
            try {
                // Remove data URL prefix if present ("data:image/png;base64,")
                String base64Data = menuItemDTO.getImageBase64().startsWith("data:image")
                        ? menuItemDTO.getImageBase64().substring(menuItemDTO.getImageBase64().indexOf(",") + 1)
                        : menuItemDTO.getImageBase64();
                Map<?, ?> uploadResult = cloudinaryService.uploadBase64Image(base64Data);
                menuItem.setImageUrl((String) uploadResult.get("secure_url"));
                menuItem.setImagePublicId((String) uploadResult.get("public_id"));
            } catch (Exception e) {
                log.error("Failed to upload base64 image for menu item: {}", menuItemDTO.getName(), e);
                throw new RuntimeException("Failed to upload image for menu item", e);
            }
        }

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

        // Handle base64 image update if provided
        if (menuItemDTO.getImageBase64() != null && !menuItemDTO.getImageBase64().isEmpty()) {
            try {
                // Delete old image if it exists
                if (menuItem.getImagePublicId() != null) {
                    cloudinaryService.deleteImage(menuItem.getImagePublicId());
                }
                // Upload new base64 image
                String base64Data = menuItemDTO.getImageBase64().startsWith("data:image")
                        ? menuItemDTO.getImageBase64().substring(menuItemDTO.getImageBase64().indexOf(",") + 1)
                        : menuItemDTO.getImageBase64();
                Map<?, ?> uploadResult = cloudinaryService.uploadBase64Image(base64Data);
                menuItem.setImageUrl((String) uploadResult.get("secure_url"));
                menuItem.setImagePublicId((String) uploadResult.get("public_id"));
            } catch (Exception e) {
                log.error("Failed to update base64 image for menu item: {}", id, e);
                throw new RuntimeException("Failed to update image for menu item", e);
            }
        }

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

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

            // Delete image from Cloudinary if exists
            if (menuItem.getImagePublicId() != null) {
                try {
                    cloudinaryService.deleteImage(menuItem.getImagePublicId());
                } catch (Exception e) {
                    log.error("Failed to delete image for menu item: {}", id, e);
                    // Continue with deletion even if image deletion fails
                }
            }

        // Delete related variants first
//        MenuItem menuItem = menuItemRepository.findById(id).get();
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


    @Override
    public Optional<MenuItemWithVariantResponseDTO> findVariantByMenuItemAndVariantId(Long menuItemId, Long variantId) {
        return variantRepository.findByMenuItemIdAndId(menuItemId, variantId)
                .map(variant -> {
                    MenuItem menuItem = variant.getMenuItem();
                    MenuItemWithVariantResponseDTO dto = new MenuItemWithVariantResponseDTO();

                    // Menu item details
                    dto.setMenuItemId(menuItem.getId());
                    dto.setMenuItemName(menuItem.getName());
                    dto.setDescription(menuItem.getDescription());
                    dto.setCategoryId(menuItem.getCategory().getId());
                    dto.setCategoryName(menuItem.getCategory().getName());
                    dto.setMenuItemAvailable(menuItem.isAvailable());


                    // Variant details
                    dto.setVariantId(variant.getId());
                    dto.setSize(variant.getSize().name());
                    dto.setVariant(variant.getVariant());
                    dto.setPrice(variant.getPrice().doubleValue());
                    dto.setStockQuantity(variant.getStockQuantity());
                    dto.setVariantAvailable(variant.isAvailable());

                    return dto;
                });
    }



    private MenuItemDTO mapToDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setCategoryId(menuItem.getCategory().getId());
        dto.setCategoryName(menuItem.getCategory().getName());
        dto.setAvailable(menuItem.isAvailable());
        dto.setImageUrl(menuItem.getImageUrl());


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


    //
    // Menu Item methods
//    @Override
//    public MenuItem createMenuItem(MenuItemRequest request) {
//        MenuCategory category = categoryRepository.findById(request.getCategoryId())
//                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
//
//        MenuItem menuItem = new MenuItem();
//        menuItem.setName(request.getName());
//        menuItem.setDescription(request.getDescription());
//        menuItem.setCategory(category);
//        menuItem.setAvailable(request.isAvailable());
//        return menuItemRepository.save(menuItem);
//    }


//    @Override
//    public List<MenuItem> getAllMenuItems() {
//        return menuItemRepository.findAll();
//    }
//
//    @Override
//    public MenuItem getMenuItemById(Long id) {
//        return menuItemRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
//    }
//
//    @Override
//    public List<MenuItem> getMenuItemsByCategory(Long categoryId) {
//        return menuItemRepository.findByCategoryId(categoryId);
//    }

//    @Override
//    public MenuItem updateMenuItem(Long id, MenuItemRequest request) {
//        MenuItem menuItem = getMenuItemById(id);
//
//        menuItem.setName(request.getName());
//        menuItem.setDescription(request.getDescription());
//
//        if (!menuItem.getCategory().getId().equals(request.getCategoryId())) {
//            MenuCategory newCategory = categoryRepository.findById(request.getCategoryId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
//            menuItem.setCategory(newCategory);
//        }
//
//        menuItem.setAvailable(request.isAvailable());
//        return menuItemRepository.save(menuItem);
//    }

//    @Override
//    @Transactional
//    public void deleteMenuItem(Long id) {
//        MenuItem menuItem = getMenuItemById(id);
//        // Check if there are variants for this menu item
//        List<MenuItemVariant> variants = variantRepository.findByMenuItem(menuItem);
//        if (!variants.isEmpty()) {
//            throw new IllegalStateException("Cannot delete menu item with associated variants");
//        }
//        menuItemRepository.delete(menuItem);
//    }

    // Menu Item Variant methods
    @Override
    public MenuItemVariant createMenuItemVariant(MenuItemVariantRequest request) {
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + request.getMenuItemId()));

        MenuItemVariant variant = new MenuItemVariant();
        variant.setMenuItem(menuItem);
        variant.setVariant(request.getVariant());
        variant.setSize(ItemSize.valueOf(request.getSize()));
        variant.setPrice(new BigDecimal(request.getPrice()));
        variant.setStockQuantity(request.getStockQuantity());
        variant.setAvailable(request.isAvailable());
        return variantRepository.save(variant);
    }

    @Override
    public List<MenuItemVariant> getAllMenuItemVariants() {
        return variantRepository.findAll();
    }

    @Override
    public MenuItemVariant getMenuItemVariantById(Long id) {
        return variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item variant not found with id: " + id));
    }

    @Override
    public List<MenuItemVariant> getVariantsByMenuItem(Long menuItemId) {
        return variantRepository.findByMenuItemId(menuItemId);
    }

    @Override
    public Integer getAvailableQuantity(Long variantId) {
        MenuItemVariant variant = getMenuItemVariantById(variantId);
        return variant.isAvailable() ? variant.getStockQuantity() : 0;
    }

    @Override
    public MenuItemVariant updateMenuItemVariant(Long id, MenuItemVariantRequest request) {
        MenuItemVariant variant = getMenuItemVariantById(id);

        if (!variant.getMenuItem().getId().equals(request.getMenuItemId())) {
            MenuItem newMenuItem = menuItemRepository.findById(request.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + request.getMenuItemId()));
            variant.setMenuItem(newMenuItem);
        }

        variant.setVariant(request.getVariant());
        variant.setSize(ItemSize.valueOf(request.getSize()));
        variant.setPrice(new BigDecimal(request.getPrice()));
        variant.setStockQuantity(request.getStockQuantity());
        variant.setAvailable(request.isAvailable());
        return variantRepository.save(variant);
    }

    @Override
    @Transactional
    public void reduceMenuItemVariantQuantity(Long id, Integer amount) {
        MenuItemVariant variant = getMenuItemVariantById(id);
        int newQuantity = variant.getStockQuantity() - amount;

        // If amount is negative, we're actually increasing the quantity (for returns/cancellations)
        if (newQuantity < 0 && amount > 0) {
            throw new IllegalStateException("Cannot reduce below zero. Current stock: " +
                    variant.getStockQuantity() + ", Requested reduction: " + amount);
        }

        variant.setStockQuantity(newQuantity);

        // If we're out of stock, set available to false
        if (newQuantity <= 0 && amount > 0) {
            variant.setAvailable(false);
        }

        variantRepository.save(variant);
    }

    @Override
    public void deleteMenuItemVariant(Long id) {
        MenuItemVariant variant = getMenuItemVariantById(id);
        variantRepository.delete(variant);
    }


}
