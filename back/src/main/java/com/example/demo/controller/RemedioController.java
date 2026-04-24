package com.example.demo.controller;

import com.example.demo.entity.RemedioEntity;
import com.example.demo.interfaces.IRemedioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/remedio")
@CrossOrigin(origins = "*")
public class RemedioController {

    @Autowired
    private IRemedioService remedioService;

    @GetMapping
    public List<RemedioEntity> listAll() {
        return remedioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RemedioEntity> getById(@PathVariable Long id) {
        Optional<RemedioEntity> remedio = remedioService.findById(id);
        return remedio.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<RemedioEntity> create(@RequestBody RemedioEntity remedio) {
        return ResponseEntity.status(HttpStatus.CREATED).body(remedioService.save(remedio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RemedioEntity> update(@PathVariable Long id, @RequestBody RemedioEntity remedio) {
        Optional<RemedioEntity> existing = remedioService.findById(id);
        if (existing.isPresent()) {
            remedio.setId(id);
            return ResponseEntity.ok(remedioService.save(remedio));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        remedioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
