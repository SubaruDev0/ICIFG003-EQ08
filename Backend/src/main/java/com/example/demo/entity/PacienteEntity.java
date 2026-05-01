package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "paciente")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreCompleto;
    private String rut;
    private String telefono;
    private String email;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = true)
    private UsuarioEntity usuario;
}