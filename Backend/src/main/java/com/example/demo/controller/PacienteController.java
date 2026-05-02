package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.PacienteEntity;
import com.example.demo.interfaces.IPacienteService;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {
    
    @Autowired
    private IPacienteService pacienteService;
    
    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            return ResponseEntity.ok(pacienteService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(pacienteService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody PacienteEntity paciente){
        try {
            return ResponseEntity.ok(pacienteService.save(paciente));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody PacienteEntity paciente, @PathVariable Long id){
        try {
            PacienteEntity existingPaciente = pacienteService.findById(id);
            if (existingPaciente == null) {
                return ResponseEntity.status(404).body("Paciente no encontrado");
            }
            existingPaciente.setNombreCompleto(paciente.getNombreCompleto());
            existingPaciente.setRut(paciente.getRut());
            existingPaciente.setTelefono(paciente.getTelefono());
            existingPaciente.setEmail(paciente.getEmail());
            existingPaciente.setUsuario(paciente.getUsuario());
            
            return ResponseEntity.ok(pacienteService.save(existingPaciente));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            PacienteEntity existingPaciente = pacienteService.findById(id);
            if (existingPaciente == null) {
                return ResponseEntity.status(404).body("Paciente no encontrado");
            }
            pacienteService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }
}