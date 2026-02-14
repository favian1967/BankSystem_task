package com.favian.bank_test_case.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private String phone;
}
