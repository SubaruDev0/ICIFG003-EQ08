package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.UsuarioEntity;
import com.example.demo.entity.enums.RolUsuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*") // Permite al frontend conectarse sin errores de CORS
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioEntity> login(@RequestBody LoginRequest loginRequest) {
        // Busca al usuario en la base de datos
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByUsername(loginRequest.getUsername());

        if (usuarioOpt.isPresent()) {
            UsuarioEntity usuario = usuarioOpt.get();
            // Compara la contraseña (actualmente en texto plano según la documentación)
            if (usuario.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(usuario); // Retorna 200 OK con los datos
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Retorna 401 Unauthorized
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioEntity> register(@RequestBody RegisterRequest registerRequest) {
        // 1. Validar si el usuario ya existe
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 2. Mapear los datos al Entity
        UsuarioEntity nuevoUsuario = new UsuarioEntity();
        nuevoUsuario.setUsername(registerRequest.getUsername());
        nuevoUsuario.setPassword(registerRequest.getPassword());
        nuevoUsuario.setImagenBase64(registerRequest.getImagenBase64());
        
        // 3. Convertir el String del Frontend a Enum de forma segura
        nuevoUsuario.setRol(RolUsuario.valueOf(registerRequest.getRol().toUpperCase()));

        // 4. Guardar en BD y retornar 201 Created
        UsuarioEntity usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
    }
}