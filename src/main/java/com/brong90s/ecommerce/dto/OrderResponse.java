package com.brong90s.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

import com.brong90s.ecommerce.entity.OrderStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String id;
    private CartResponse cart;
    private OrderStatus orderStatus;
    private Date createdAt;
    private Date processedAt;
    private Long total;
}
