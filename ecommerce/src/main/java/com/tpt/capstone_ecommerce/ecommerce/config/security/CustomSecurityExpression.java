package com.tpt.capstone_ecommerce.ecommerce.config.security;

import com.tpt.capstone_ecommerce.ecommerce.constant.ShopErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.ShopRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomSecurityExpression {
    private final UserRepository userRepository;

    private final ShopRepository shopRepository;

    public CustomSecurityExpression(UserRepository userRepository, ShopRepository shopRepository) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    // Check tài nguyên của user
    public boolean isOwner(String userId, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(UserErrorConstant.USER_NOT_FOUND));
        return user.getId().equals(userId);
    }

    // Check tài nguyên của shop
    public boolean isShopOwner(String shopId, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(UserErrorConstant.USER_NOT_FOUND));
        if (user == null) return false;

        Shop shop = shopRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));
        return shop.getId().equals(shopId);
    }
}
