package com.example.demo.interfaces;

import com.example.demo.entity.RemedioEntity;
import java.util.List;
import java.util.Optional;

public interface IRemedioService {
    List<RemedioEntity> findAll();
    Optional<RemedioEntity> findById(Long id);
    RemedioEntity save(RemedioEntity remedio);
    void deleteById(Long id);
}
