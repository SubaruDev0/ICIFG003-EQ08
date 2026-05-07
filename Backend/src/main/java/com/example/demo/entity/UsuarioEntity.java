package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.example.demo.entity.enums.RolUsuario;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

import com.example.demo.entity.enums.RolUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    @Column(name = "fecha_creacion", updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
}