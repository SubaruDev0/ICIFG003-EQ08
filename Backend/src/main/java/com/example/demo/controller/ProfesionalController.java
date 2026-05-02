package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.ProfesionalEntity;
import com.example.demo.interfaces.IProfesionalService;

@RestController
@RequestMapping("/api/v1/profesionales")
public class ProfesionalController {
    
    @Autowired
    private IProfesionalService profesionalService;
    
    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            return ResponseEntity.ok(profesionalService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(profesionalService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ProfesionalEntity profesional){
        try {
            return ResponseEntity.ok(profesionalService.save(profesional));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody ProfesionalEntity profesional, @PathVariable Long id){
        try {
            ProfesionalEntity existingProfesional = profesionalService.findById(id);
            if (existingProfesional == null) {
                return ResponseEntity.status(404).body("Profesional no encontrado");
            }
            existingProfesional.setNombreCompleto(profesional.getNombreCompleto());
            existingProfesional.setImagenBase64(profesional.getImagenBase64());
            existingProfesional.setServicio(profesional.getServicio());
            
            return ResponseEntity.ok(profesionalService.save(existingProfesional));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            ProfesionalEntity existingProfesional = profesionalService.findById(id);
            if (existingProfesional == null) {
                return ResponseEntity.status(404).body("Profesional no encontrado");
            }
            profesionalService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }
}