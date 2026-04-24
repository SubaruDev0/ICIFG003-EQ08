package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "remedio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemedioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreCientifico;
    private String marca;
    private Double gramos;
    private int anio;
    private char tipo; // 'M'arca, 'B'ioequivalente, 'G'enericos
    private boolean controlado;
}
