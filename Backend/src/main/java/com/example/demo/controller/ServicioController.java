package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.ServicioEntity;
import com.example.demo.interfaces.IServicioService;

@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioController {
    
    @Autowired
    private IServicioService servicioService;
    
    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            return ResponseEntity.ok(servicioService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(servicioService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ServicioEntity servicio){
        try {
            return ResponseEntity.ok(servicioService.save(servicio));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody ServicioEntity servicio, @PathVariable Long id){
        try {
            ServicioEntity existingServicio = servicioService.findById(id);
            if (existingServicio == null) {
                return ResponseEntity.status(404).body("Servicio no encontrado");
            }
            existingServicio.setNombre(servicio.getNombre());
            
            return ResponseEntity.ok(servicioService.save(existingServicio));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            ServicioEntity existingServicio = servicioService.findById(id);
            if (existingServicio == null) {
                return ResponseEntity.status(404).body("Servicio no encontrado");
            }
            servicioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }
}