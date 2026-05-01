package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.TurnoEntity;
import com.example.demo.interfaces.ITurnoService;
import com.example.demo.repository.TurnoRepository;

@Service
public class TurnoService implements ITurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Override
    public List<TurnoEntity> findAll() {
        return (List<TurnoEntity>) turnoRepository.findAll();
    }

    @Override
    public TurnoEntity findById(Long id) {
        return turnoRepository.findById(id).orElse(null);
    }

    @Override
    public TurnoEntity save(TurnoEntity turno) {
        return turnoRepository.save(turno);
    }

    @Override
    public void deleteById(Long id) {
        turnoRepository.deleteById(id);
    }

}