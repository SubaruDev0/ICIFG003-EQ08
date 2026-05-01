package com.example.demo.interfaces;

import java.util.List;

import com.example.demo.entity.TurnoEntity;

public interface ITurnoService {
    
    List<TurnoEntity> findAll();

    TurnoEntity findById(Long id);

    TurnoEntity save(TurnoEntity turno);
    
    void deleteById(Long id);
    
}