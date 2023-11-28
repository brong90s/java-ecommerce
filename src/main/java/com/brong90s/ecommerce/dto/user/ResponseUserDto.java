package com.brong90s.ecommerce.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserDto {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String mobileNumber;
}
