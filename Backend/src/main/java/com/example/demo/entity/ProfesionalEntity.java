package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profesional")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfesionalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;

    @Column(columnDefinition = "TEXT")
    private String imagenBase64;

    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = true)
    private ServicioEntity servicio;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    @JsonIgnore
    private UsuarioEntity usuario;
}