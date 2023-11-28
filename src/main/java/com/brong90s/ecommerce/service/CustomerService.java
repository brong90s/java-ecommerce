package com.brong90s.ecommerce.service;

import java.util.List;

import com.brong90s.ecommerce.dto.cart.CartResponse;
import com.brong90s.ecommerce.dto.order.OrderResponse;
import com.brong90s.ecommerce.entity.CartItem;
import com.brong90s.ecommerce.entity.Order;
import com.brong90s.ecommerce.entity.Product;

import jakarta.servlet.http.HttpServletRequest;

public interface CustomerService {
  List<Product> fetchAllProducts();

  Product fetchProductById(String productId);

  CartResponse addProductInCart(String productId, HttpServletRequest request);

  String removeProductFromCart(String cartItemId);

  CartItem increaseDecreaseProductQuantity(String cartItemId, Integer value);

  List<Order> fetchAllOrdersByUserId(HttpServletRequest request);

  OrderResponse createOrder(HttpServletRequest request);

  CartResponse fetchCartByUserId(HttpServletRequest request);
}
