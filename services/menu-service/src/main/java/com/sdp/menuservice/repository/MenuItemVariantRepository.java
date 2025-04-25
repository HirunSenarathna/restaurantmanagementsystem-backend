package com.sdp.menuservice.repository;

import com.sdp.menuservice.model.ItemSize;
import com.sdp.menuservice.model.MenuItem;
import com.sdp.menuservice.model.MenuItemVariant;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemVariantRepository extends JpaRepository<MenuItemVariant, Long> {

    List<MenuItemVariant> findByMenuItem(MenuItem menuItem);
    List<MenuItemVariant> findByMenuItemAndAvailableTrue(MenuItem menuItem);
    Optional<MenuItemVariant> findByMenuItemAndSizeAndVariant(MenuItem menuItem, ItemSize size, String variant);
}
