package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.ProfesionalEntity;
import com.example.demo.entity.ServicioEntity;
import com.example.demo.entity.UsuarioEntity;
import com.example.demo.entity.enums.RolUsuario;
import com.example.demo.repository.ProfesionalRepository;
import com.example.demo.repository.ServicioRepository;
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
    private final ProfesionalRepository profesionalRepository;
    private final ServicioRepository servicioRepository;

    public AuthController(
        UsuarioRepository usuarioRepository,
        ProfesionalRepository profesionalRepository,
        ServicioRepository servicioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.profesionalRepository = profesionalRepository;
        this.servicioRepository = servicioRepository;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()
            || registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()
            || registerRequest.getRol() == null || registerRequest.getRol().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("username, password y rol son obligatorios");
        }

        if (usuarioRepository.existsByUsername(registerRequest.getUsername().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El username ya existe");
        }

        RolUsuario rol;
        try {
            String rolNormalizado = registerRequest.getRol().trim().toUpperCase();
            if ("USER".equals(rolNormalizado)) {
                rolNormalizado = "PACIENTE";
            }
            rol = RolUsuario.valueOf(rolNormalizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol inválido");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(registerRequest.getUsername().trim());
        usuario.setPassword(registerRequest.getPassword());
        usuario.setRol(rol);
        usuario.setImagenBase64(registerRequest.getImagenBase64());
        UsuarioEntity usuarioCreado = usuarioRepository.save(usuario);

        if (rol == RolUsuario.ADMIN) {
            ProfesionalEntity profesional = new ProfesionalEntity();
            profesional.setNombreCompleto(usuarioCreado.getUsername());
            profesional.setImagenBase64(registerRequest.getImagenBase64());

            if (registerRequest.getServicioId() != null) {
                Optional<ServicioEntity> servicioOpt = servicioRepository.findById(registerRequest.getServicioId());
                if (servicioOpt.isPresent()) {
                    profesional.setServicio(servicioOpt.get());
                }
            }

            profesionalRepository.save(profesional);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }
}
