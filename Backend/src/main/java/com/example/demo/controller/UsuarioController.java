package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.UsuarioEntity;
import com.example.demo.interfaces.IUsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            return ResponseEntity.ok(usuarioService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(usuarioService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody UsuarioEntity usuario){
        try {
            return ResponseEntity.ok(usuarioService.save(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody UsuarioEntity usuario, @PathVariable Long id){
        try {
            UsuarioEntity existingUsuario = usuarioService.findById(id);
            if (existingUsuario == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            existingUsuario.setUsername(usuario.getUsername());
            existingUsuario.setPassword(usuario.getPassword());
            existingUsuario.setRol(usuario.getRol());
            
            return ResponseEntity.ok(usuarioService.save(existingUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            UsuarioEntity existingUsuario = usuarioService.findById(id);
            if (existingUsuario == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }
}