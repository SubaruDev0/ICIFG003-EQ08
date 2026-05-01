package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TurnoEntity;

@Repository
public interface TurnoRepository extends CrudRepository<TurnoEntity, Long> {

}