package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.demo.entity.PersonaEntity;
import com.example.demo.interfaces.IPersonaService;

@RestController
@RequestMapping("/api/v1/entities/personas")
@CrossOrigin(origins = "*")
public class PersonaController {
	
	@Autowired
	private IPersonaService personaService;
	
	@GetMapping
	public ResponseEntity<?> findAll(){
		try {
			return ResponseEntity.ok(personaService.findAll());
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id){
		try {
			return ResponseEntity.ok(personaService.findById(id));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e);
		}
	}

	@PostMapping
	public ResponseEntity<?> save(@RequestBody PersonaEntity persona){
		try {
			return ResponseEntity.ok(personaService.save(persona));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@RequestBody PersonaEntity persona, @PathVariable Long id){
		try {
			PersonaEntity existingPersona = personaService.findById(id);
			if (existingPersona == null) {
				return ResponseEntity.status(404).body("Persona no encontrada");
			}
			existingPersona.setNombre(persona.getNombre());
			return ResponseEntity.ok(personaService.save(existingPersona));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		try {
			PersonaEntity existingPersona = personaService.findById(id);
			if (existingPersona == null) {
				return ResponseEntity.status(404).body("Persona no encontrada");
			}
			personaService.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e);
		}
	}

}