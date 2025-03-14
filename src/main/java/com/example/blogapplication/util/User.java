package com.example.blogapplication.util;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    @NotBlank(message="Email is required")
    @Email(message = "Email is not correct")
     private String email;
    @NotBlank(message = "Password is required")
     private String password;
}
