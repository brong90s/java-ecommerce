package com.brong90s.ecommerce.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.brong90s.ecommerce.dto.order.OrderResponse;
import com.brong90s.ecommerce.dto.product.ProductRequest;
import com.brong90s.ecommerce.entity.Category;
import com.brong90s.ecommerce.entity.Order;
import com.brong90s.ecommerce.entity.Product;
import com.brong90s.ecommerce.entity.enums.OrderStatus;

public interface ShopkeeperService {
  Product addProduct(ProductRequest request, MultipartFile[] images);

  List<Category> fetchAllCategories();

  Product addImageToProduct(String productId, MultipartFile[] images);

  List<Product> fetchAllProducts();

  Product fetchProductById(String productId);

  Product updateProductById(ProductRequest request, MultipartFile[] images, String productId);

  List<Order> fetchAllOrders();

  Order fetchOrderById(String orderId);

  OrderResponse processOrderById(OrderStatus orderStatus, String orderId);

  Category createCategory(String category);
}
