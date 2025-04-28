package com.sdp.menuservice.service;

import com.sdp.menuservice.dto.MenuItemDTO;
import com.sdp.menuservice.dto.MenuItemWithVariantResponseDTO;
import com.sdp.menuservice.dto.Request.MenuItemRequest;
import com.sdp.menuservice.dto.Request.MenuItemRequestDTO;
import com.sdp.menuservice.dto.Request.MenuItemVariantRequest;
import com.sdp.menuservice.dto.Request.StockUpdateRequestDTO;
import com.sdp.menuservice.model.MenuItem;
import com.sdp.menuservice.model.MenuItemVariant;

import java.util.List;
import java.util.Optional;

public interface MenuItemService {

    List<MenuItemDTO> getAllMenuItems();
    List<MenuItemDTO> getMenuItemsByCategory(Long categoryId);
    MenuItemDTO getMenuItemById(Long id);
    MenuItemDTO createMenuItem(MenuItemRequestDTO menuItemDTO);
    MenuItemDTO updateMenuItem(Long id, MenuItemRequestDTO menuItemDTO);
    void deleteMenuItem(Long id);
    MenuItemDTO updateMenuItemStock(Long id, Long variantId, StockUpdateRequestDTO stockUpdateRequest);
    MenuItemDTO updateMenuItemAvailability(Long id, boolean available);

    Optional<MenuItemWithVariantResponseDTO> findVariantByMenuItemAndVariantId(Long menuItemId, Long variantId);


    // Menu Item methods
    MenuItem createMenuItem(MenuItemRequest request);



    // Menu Item Variant methods
    MenuItemVariant createMenuItemVariant(MenuItemVariantRequest request);
    List<MenuItemVariant> getAllMenuItemVariants();
    MenuItemVariant getMenuItemVariantById(Long id);
    List<MenuItemVariant> getVariantsByMenuItem(Long menuItemId);
    Integer getAvailableQuantity(Long variantId);
    MenuItemVariant updateMenuItemVariant(Long id, MenuItemVariantRequest request);
    void reduceMenuItemVariantQuantity(Long id, Integer amount);
    void deleteMenuItemVariant(Long id);
}
