package com.brong90s.ecommerce.controller;

import com.brong90s.ecommerce.dto.OrderResponse;
import com.brong90s.ecommerce.dto.ProductRequest;
import com.brong90s.ecommerce.entity.Category;
import com.brong90s.ecommerce.entity.Order;
import com.brong90s.ecommerce.entity.OrderStatus;
import com.brong90s.ecommerce.entity.Product;
import com.brong90s.ecommerce.service.impl.ShopkeeperServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shopkeeper")
@RequiredArgsConstructor
public class ShopkeeperController {
    private final ShopkeeperServiceImpl shopkeeperService;
    private final ObjectMapper objectMapper;

    @PostMapping("/products/add")
    public ResponseEntity<Product> addProduct(
            @RequestParam("images") MultipartFile[] images,
            @RequestParam(value = "propertyJsonData") String productJsonData) {
        ProductRequest request;
        // convert jason data to product request object
        try {
            request = objectMapper.readValue(productJsonData, ProductRequest.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to phase json to object");
        }

        // add new product and return a response
        return ResponseEntity.created(URI.create("")).body(
                this.shopkeeperService.addProduct(request, images));

    }

    @GetMapping("/categories")
    public List<Category> fetchAllCategories() {
        return this.shopkeeperService.fetchAllCategories();
    }

    @PostMapping("/category/add/{category}")
    public Category addCategory(@PathVariable String category) {
        return this.shopkeeperService.createCategory(category);
    }

    @PostMapping("/product/images/add/{productId}")
    public Product addImageToProduct(
            @PathVariable String productId,
            @RequestParam("images") MultipartFile[] images) {
        return this.shopkeeperService.addImageToProduct(productId, images);
    }

    @GetMapping("/")
    public List<Product> fetchAllProducts() {
        return this.shopkeeperService.fetchAllProducts();
    }

    @GetMapping("/product/{productId}")
    public Product fetchProductById(@PathVariable String productId) {
        return this.shopkeeperService.fetchProductById(productId);
    }

    @PutMapping("/product/{productId}")
    public Product updateProductById(
            @RequestParam("images") MultipartFile[] images,
            @RequestParam(value = "propertyJsonData") String productJsonData,
            @PathVariable String productId) {
        ProductRequest request;
        // convert jason data to product request object
        try {
            request = objectMapper.readValue(productJsonData, ProductRequest.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to phase json to object");
        }

        return this.shopkeeperService.updateProductById(request, images, productId);
    }

    @GetMapping("/orders")
    public List<Order> fetchAllOrders() {
        return this.shopkeeperService.fetchAllOrders();
    }

    @GetMapping("/oder/{orderId}")
    public Order fetchOderById(@PathVariable String orderId) {
        return this.shopkeeperService.fetchOderById(orderId);
    }

    @PostMapping("/order/process/{orderId}")
    public OrderResponse processOrderById(
            @RequestParam OrderStatus orderStatus,
            @PathVariable String orderId) {
        return this.shopkeeperService.processOrderById(orderStatus, orderId);
    }
}
