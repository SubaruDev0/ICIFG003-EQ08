package com.example.demo.service;

import com.example.demo.entity.RemedioEntity;
import com.example.demo.interfaces.IRemedioService;
import com.example.demo.repository.RemedioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RemedioServiceImp implements IRemedioService {

    @Autowired
    private RemedioRepository remedioRepository;

    @Override
    public List<RemedioEntity> findAll() {
        return remedioRepository.findAll();
    }

    @Override
    public Optional<RemedioEntity> findById(Long id) {
        return remedioRepository.findById(id);
    }

    @Override
    public RemedioEntity save(RemedioEntity remedio) {
        return remedioRepository.save(remedio);
    }

    @Override
    public void deleteById(Long id) {
        remedioRepository.deleteById(id);
    }
}
