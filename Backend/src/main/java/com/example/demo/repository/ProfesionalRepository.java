package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ProfesionalEntity;

@Repository
public interface ProfesionalRepository extends CrudRepository<ProfesionalEntity, Long> {

}