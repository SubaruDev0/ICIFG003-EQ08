package com.example.demo.interfaces;

import java.util.List;

import com.example.demo.entity.ServicioEntity;

public interface IServicioService {
    
    List<ServicioEntity> findAll();

    ServicioEntity findById(Long id);

    ServicioEntity save(ServicioEntity servicio);
    
    void deleteById(Long id);
    
}