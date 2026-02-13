package com.favian.bank_test_case.dto;

import lombok.Data;

@Data
public class LoginRequest {
    
    private String email;
    
    private String password;
}
