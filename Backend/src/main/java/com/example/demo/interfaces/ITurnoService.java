package com.example.demo.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.entity.TurnoEntity;

public interface ITurnoService {
    
    List<TurnoEntity> findAll();

    TurnoEntity findById(Long id);

    TurnoEntity save(TurnoEntity turno);

    List<String> obtenerHorariosDisponibles(Long servicioId, LocalDate fecha);
    
    void deleteById(Long id);
    
}
