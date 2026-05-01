package com.example.demo.interfaces;

import java.util.List;

import com.example.demo.entity.UsuarioEntity;

public interface IUsuarioService {
    
    List<UsuarioEntity> findAll();

    UsuarioEntity findById(Long id);

    UsuarioEntity save(UsuarioEntity usuario);
    
    void deleteById(Long id);
    
}