package com.example.demo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String rol;
    private String imagenBase64;
    private Long servicioId;
}
