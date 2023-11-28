package com.brong90s.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.brong90s.ecommerce.entity.enums.OrderStatus;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Order {
    @Id
    private String id;
    private Cart cart;
    private OrderStatus orderStatus;
    private Date createdAt;
    private Date processedAt;
    private Long total;
}
