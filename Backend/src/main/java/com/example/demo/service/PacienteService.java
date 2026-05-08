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
        if (paciente.getRut() != null) {
            java.util.Optional<PacienteEntity> existente = pacienteRepository.findByRut(paciente.getRut());
            if (existente.isPresent()) {
                PacienteEntity p = existente.get();
                p.setNombreCompleto(paciente.getNombreCompleto());
                p.setTelefono(paciente.getTelefono());
                p.setEmail(paciente.getEmail());
                return pacienteRepository.save(p);
            }
        }
        return pacienteRepository.save(paciente);
    }

    @Override
    public void deleteById(Long id) {
        pacienteRepository.deleteById(id);
    }

}