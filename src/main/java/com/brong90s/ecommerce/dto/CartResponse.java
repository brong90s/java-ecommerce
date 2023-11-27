package com.brong90s.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.brong90s.ecommerce.entity.CartItem;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private String id;
    private LoggedUserResponse user;
    private List<CartItem> cartItems;
}
