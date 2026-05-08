package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PacienteEntity;

@Repository
public interface PacienteRepository extends CrudRepository<PacienteEntity, Long> {
    java.util.Optional<PacienteEntity> findByRut(String rut);
}