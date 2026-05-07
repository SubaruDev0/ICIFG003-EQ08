package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TurnoEntity;

import java.util.Date;
import java.util.List;

@Repository
public interface TurnoRepository extends CrudRepository<TurnoEntity, Long> {
    List<TurnoEntity> findByServicioIdAndFecha(Long servicioId, Date fecha);
    
}