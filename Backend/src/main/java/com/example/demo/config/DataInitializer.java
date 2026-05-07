package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.entity.UsuarioEntity;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.entity.enums.RolUsuario;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Value("${APP_ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${APP_ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    @Value("${APP_ADMIN_ROLE:ADMIN}")
    private String adminRole;

    public DataInitializer(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByUsername(adminUsername)) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setUsername(adminUsername);
            admin.setPassword(adminPassword);
            admin.setRol(RolUsuario.valueOf(adminRole.toUpperCase()));
            usuarioRepository.save(admin);
        }
    }
}
