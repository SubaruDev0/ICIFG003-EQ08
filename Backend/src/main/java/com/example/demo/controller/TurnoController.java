package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.TurnoEntity;
import com.example.demo.interfaces.ITurnoService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoController {
    
    @Autowired
    private ITurnoService turnoService;
    
    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            return ResponseEntity.ok(turnoService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(turnoService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TurnoEntity turno){
        try {
            if (turno.getServicio() == null || turno.getServicio().getId() == null) {
                return ResponseEntity.badRequest().body("servicio.id es obligatorio");
            }
            if (turno.getFecha() == null || turno.getHorario() == null || turno.getHorario().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("fecha y horario son obligatorios");
            }
            return ResponseEntity.ok(turnoService.save(turno));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> horariosDisponibles(
        @RequestParam(required = false) Long servicioId,
        @RequestParam String fecha
    ) {
        try {
            return ResponseEntity.ok(turnoService.obtenerHorariosDisponibles(servicioId, LocalDate.parse(fecha)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TurnoEntity turno, @PathVariable Long id){
        try {
            TurnoEntity existingTurno = turnoService.findById(id);
            if (existingTurno == null) {
                return ResponseEntity.status(404).body("Turno no encontrado");
            }
            existingTurno.setFecha(turno.getFecha());
            existingTurno.setHorario(turno.getHorario());
            existingTurno.setMensajeAdicional(turno.getMensajeAdicional());
            existingTurno.setPaciente(turno.getPaciente());
            existingTurno.setServicio(turno.getServicio());
            
            return ResponseEntity.ok(turnoService.save(existingTurno));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            TurnoEntity existingTurno = turnoService.findById(id);
            if (existingTurno == null) {
                return ResponseEntity.status(404).body("Turno no encontrado");
            }
            turnoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e);
        }
    }
}
