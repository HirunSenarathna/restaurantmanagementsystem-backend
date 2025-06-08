package com.sdp.analyticsservice.client;

import com.sdp.analyticsservice.dto.MenuCategoryDTO;
import com.sdp.analyticsservice.dto.MenuItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "menu-service",contextId = "menuServiceClient",  url = "${application.config.menu-url}")
public interface MenuServiceClient {
    @GetMapping("/items/{id}")
    MenuItemDTO getMenuItemById(@PathVariable("id") Long id);

    @GetMapping("/categories")
    List<MenuCategoryDTO> getAllCategories();
}
