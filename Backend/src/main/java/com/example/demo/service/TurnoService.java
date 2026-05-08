package com.example.demo.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (turno.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (turno.getServicio() == null || turno.getServicio().getId() == null) {
            throw new IllegalArgumentException("El servicio es obligatorio");
        }
        if (turno.getHorario() == null || turno.getHorario().trim().isEmpty()) {
            throw new IllegalArgumentException("El horario es obligatorio");
        }

        LocalDate fecha = Instant.ofEpochMilli(turno.getFecha().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        List<String> disponibles = obtenerHorariosDisponibles(turno.getServicio().getId(), fecha);
        if (!disponibles.contains(turno.getHorario())) {
            throw new IllegalArgumentException("El horario no está disponible");
        }
        return turnoRepository.save(turno);
    }

    @Override
    public List<String> obtenerHorariosDisponibles(Long servicioId, LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        LocalDate hoy = LocalDate.now();
        if (fecha.isBefore(hoy)) {
            throw new IllegalArgumentException("No se permiten fechas pasadas");
        }
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No hay atención los domingos");
        }

        List<String> bloquesBase = new ArrayList<>();
        for (int hora = 9; hora <= 18; hora++) {
            bloquesBase.add(String.format("%02d:00", hora));
            if (hora < 18) {
                bloquesBase.add(String.format("%02d:30", hora));
            }
        }

        if (servicioId == null) {
            return bloquesBase;
        }

        Date fechaDate = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Set<String> ocupados = turnoRepository.findByServicioIdAndFecha(servicioId, fechaDate)
            .stream()
            .map(TurnoEntity::getHorario)
            .collect(Collectors.toSet());

        return bloquesBase.stream()
            .filter(h -> !ocupados.contains(h))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        turnoRepository.deleteById(id);
    }

}
