package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ProfesionalEntity;
import com.example.demo.interfaces.IProfesionalService;
import com.example.demo.repository.ProfesionalRepository;

@Service
public class ProfesionalService implements IProfesionalService {

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Override
    public List<ProfesionalEntity> findAll() {
        List<ProfesionalEntity> profesionales = (List<ProfesionalEntity>) profesionalRepository.findAll();
        profesionales.forEach(p -> {
            if (p.getUsuario() != null && p.getUsuario().getImagenBase64() != null && !p.getUsuario().getImagenBase64().isEmpty()) {
                p.setImagenBase64(p.getUsuario().getImagenBase64());
            }
        });
        return profesionales;
    }

    @Override
    public ProfesionalEntity findById(Long id) {
        return profesionalRepository.findById(id).orElse(null);
    }

    @Override
    public ProfesionalEntity save(ProfesionalEntity profesional) {
        return profesionalRepository.save(profesional);
    }

    @Override
    public void deleteById(Long id) {
        profesionalRepository.deleteById(id);
    }

}