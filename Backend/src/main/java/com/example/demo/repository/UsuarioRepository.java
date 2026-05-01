package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.UsuarioEntity;

@Repository
public interface UsuarioRepository extends CrudRepository<UsuarioEntity, Long> {

}