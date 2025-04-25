package com.sdp.menuservice.service;

import com.sdp.menuservice.dto.MenuItemDTO;
import com.sdp.menuservice.dto.Request.MenuItemRequestDTO;
import com.sdp.menuservice.dto.Request.StockUpdateRequestDTO;

import java.util.List;

public interface MenuItemService {

    List<MenuItemDTO> getAllMenuItems();
    List<MenuItemDTO> getMenuItemsByCategory(Long categoryId);
    MenuItemDTO getMenuItemById(Long id);
    MenuItemDTO createMenuItem(MenuItemRequestDTO menuItemDTO);
    MenuItemDTO updateMenuItem(Long id, MenuItemRequestDTO menuItemDTO);
    void deleteMenuItem(Long id);
    MenuItemDTO updateMenuItemStock(Long id, Long variantId, StockUpdateRequestDTO stockUpdateRequest);
    MenuItemDTO updateMenuItemAvailability(Long id, boolean available);
}
