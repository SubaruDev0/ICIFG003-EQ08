package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PacienteEntity;
import com.example.demo.interfaces.IPacienteService;
import com.example.demo.repository.PacienteRepository;

@Service
public class PacienteService implements IPacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public List<PacienteEntity> findAll() {
        return (List<PacienteEntity>) pacienteRepository.findAll();
    }

    @Override
    public PacienteEntity findById(Long id) {
        return pacienteRepository.findById(id).orElse(null);
    }

    @Override
    public PacienteEntity save(PacienteEntity paciente) {
        return pacienteRepository.save(paciente);
    }

    @Override
    public void deleteById(Long id) {
        pacienteRepository.deleteById(id);
    }

}