package com.example.demo.interfaces;

import java.util.List;

import com.example.demo.entity.PacienteEntity;

public interface IPacienteService {
    
    List<PacienteEntity> findAll();

    PacienteEntity findById(Long id);

    PacienteEntity save(PacienteEntity paciente);
    
    void deleteById(Long id);
    
}