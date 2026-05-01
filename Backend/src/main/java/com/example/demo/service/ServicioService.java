package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ServicioEntity;
import com.example.demo.interfaces.IServicioService;
import com.example.demo.repository.ServicioRepository;

@Service
public class ServicioService implements IServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Override
    public List<ServicioEntity> findAll() {
        return (List<ServicioEntity>) servicioRepository.findAll();
    }

    @Override
    public ServicioEntity findById(Long id) {
        return servicioRepository.findById(id).orElse(null);
    }

    @Override
    public ServicioEntity save(ServicioEntity servicio) {
        return servicioRepository.save(servicio);
    }

    @Override
    public void deleteById(Long id) {
        servicioRepository.deleteById(id);
    }

}