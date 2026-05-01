package com.example.demo.interfaces;

import java.util.List;

import com.example.demo.entity.ProfesionalEntity;

public interface IProfesionalService {
    
    List<ProfesionalEntity> findAll();

    ProfesionalEntity findById(Long id);

    ProfesionalEntity save(ProfesionalEntity profesional);
    
    void deleteById(Long id);
    
}