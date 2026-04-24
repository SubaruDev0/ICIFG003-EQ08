package com.example.demo.repository;

import com.example.demo.entity.RemedioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemedioRepository extends JpaRepository<RemedioEntity, Long> {
}
