package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.entity.enums.EstadoTurno;
import com.example.demo.entity.enums.RolUsuario;
import com.example.demo.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/seed")
public class SeedController {

    // Fotos de servicios (picsum.photos — siempre disponible, seed = imagen consistente)
    private static final String IMG_ODONTOLOGIA  = "https://picsum.photos/seed/odontologia/600/400";
    private static final String IMG_ORTODONCIA   = "https://picsum.photos/seed/ortodoncia/600/400";
    private static final String IMG_IMPLANTES    = "https://picsum.photos/seed/implantes/600/400";
    private static final String IMG_ENDODONCIA   = "https://picsum.photos/seed/endodoncia/600/400";
    private static final String IMG_BLANQUEA     = "https://picsum.photos/seed/blanqueamiento/600/400";

    // Avatares de profesionales (ui-avatars.com — genera iniciales, siempre disponible)
    private static final String PROF_1 = "https://ui-avatars.com/api/?name=Admin+Clinica&background=0fa49c&color=fff&size=200";
    private static final String PROF_2 = "https://ui-avatars.com/api/?name=Sofia+Martinez&background=0d7a75&color=fff&size=200";
    private static final String PROF_3 = "https://ui-avatars.com/api/?name=Carlos+Gomez&background=0a5c58&color=fff&size=200";
    private static final String PROF_4 = "https://ui-avatars.com/api/?name=Ana+Lopez&background=12b5ae&color=fff&size=200";

    // Avatares de pacientes
    private static final String PAC_1  = "https://ui-avatars.com/api/?name=Juan+Perez&background=5b8dee&color=fff&size=200";
    private static final String PAC_2  = "https://ui-avatars.com/api/?name=Maria+Garcia&background=e05b9a&color=fff&size=200";

    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final PacienteRepository pacienteRepository;
    private final TurnoRepository turnoRepository;
    private final JdbcTemplate jdbcTemplate;

    public SeedController(
        UsuarioRepository usuarioRepository,
        ServicioRepository servicioRepository,
        ProfesionalRepository profesionalRepository,
        PacienteRepository pacienteRepository,
        TurnoRepository turnoRepository,
        JdbcTemplate jdbcTemplate
    ) {
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.profesionalRepository = profesionalRepository;
        this.pacienteRepository = pacienteRepository;
        this.turnoRepository = turnoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping
    public ResponseEntity<?> seedAll() {
        if (usuarioRepository.count() > 0 || servicioRepository.count() > 0) {
            return ResponseEntity.badRequest()
                .body("La DB ya tiene datos. Corré DELETE /api/v1/seed primero para resetear.");
        }

        // Servicios con fotos reales
        ServicioEntity odontologia    = servicioRepository.save(new ServicioEntity(null, "Odontologia General", IMG_ODONTOLOGIA));
        ServicioEntity ortodoncia     = servicioRepository.save(new ServicioEntity(null, "Ortodoncia", IMG_ORTODONCIA));
        ServicioEntity implantes      = servicioRepository.save(new ServicioEntity(null, "Implantes Dentales", IMG_IMPLANTES));
        ServicioEntity endodoncia     = servicioRepository.save(new ServicioEntity(null, "Endodoncia", IMG_ENDODONCIA));
        ServicioEntity blanqueamiento = servicioRepository.save(new ServicioEntity(null, "Blanqueamiento Dental", IMG_BLANQUEA));

        // Usuarios
        UsuarioEntity uAdmin  = usuarioRepository.save(crearUsuario("admin",       "admin123",  RolUsuario.ADMIN,       PROF_1));
        UsuarioEntity uPro1   = usuarioRepository.save(crearUsuario("dra.martinez","prof123",   RolUsuario.PROFESIONAL, PROF_2));
        UsuarioEntity uPro2   = usuarioRepository.save(crearUsuario("dr.gomez",    "prof123",   RolUsuario.PROFESIONAL, PROF_3));
        UsuarioEntity uPro3   = usuarioRepository.save(crearUsuario("dra.lopez",   "prof123",   RolUsuario.PROFESIONAL, PROF_4));
        UsuarioEntity uPac1   = usuarioRepository.save(crearUsuario("paciente1",   "pac123",    RolUsuario.PACIENTE,    PAC_1));
        UsuarioEntity uPac2   = usuarioRepository.save(crearUsuario("paciente2",   "pac123",    RolUsuario.PACIENTE,    PAC_2));

        // Profesionales vinculados a servicio y con foto
        profesionalRepository.save(new ProfesionalEntity(null, "Dra. Sofia Martinez", PROF_2, ortodoncia));
        profesionalRepository.save(new ProfesionalEntity(null, "Dr. Carlos Gomez",    PROF_3, implantes));
        profesionalRepository.save(new ProfesionalEntity(null, "Dra. Ana Lopez",      PROF_4, endodoncia));
        profesionalRepository.save(new ProfesionalEntity(null, "Admin Clinica",       PROF_1, odontologia));

        // Pacientes vinculados a su usuario
        PacienteEntity pac1 = new PacienteEntity();
        pac1.setNombreCompleto("Juan Perez");
        pac1.setRut("12345678-9");
        pac1.setTelefono("+56911111111");
        pac1.setEmail("juan.perez@mail.com");
        pac1.setUsuario(uPac1);
        pac1 = pacienteRepository.save(pac1);

        PacienteEntity pac2 = new PacienteEntity();
        pac2.setNombreCompleto("Maria Garcia");
        pac2.setRut("98765432-1");
        pac2.setTelefono("+56922222222");
        pac2.setEmail("maria.garcia@mail.com");
        pac2.setUsuario(uPac2);
        pac2 = pacienteRepository.save(pac2);

        // Turnos con lógica variada de estados y servicios
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(1), "09:00", "Primera consulta de ortodoncia",       pac1, ortodoncia,     EstadoTurno.CONFIRMADO));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(2), "10:30", "Revisión de brackets",                 pac1, ortodoncia,     EstadoTurno.PENDIENTE));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(3), "11:00", "Dolor en molar inferior",              pac2, odontologia,    EstadoTurno.CONFIRMADO));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(4), "15:00", "Evaluación para implante superior",    pac2, implantes,      EstadoTurno.PENDIENTE));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(5), "16:30", "Sesión de blanqueamiento estético",    pac1, blanqueamiento, EstadoTurno.CONFIRMADO));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(6), "09:30", "Tratamiento de conducto molar",        pac2, endodoncia,     EstadoTurno.PENDIENTE));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(7), "14:00", "Control semestral odontología general", pac1, odontologia,   EstadoTurno.CONFIRMADO));

        return ResponseEntity.ok(
            "Seed completo:\n" +
            "  5 servicios con fotos\n" +
            "  6 usuarios (admin/admin123 | dra.martinez/prof123 | dr.gomez/prof123 | dra.lopez/prof123 | paciente1/pac123 | paciente2/pac123)\n" +
            "  4 profesionales\n" +
            "  2 pacientes\n" +
            "  7 turnos (confirmados y pendientes)"
        );
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> resetAll() {
        jdbcTemplate.execute("TRUNCATE TABLE turno, paciente, profesional, servicio, usuario RESTART IDENTITY CASCADE");
        return ResponseEntity.ok("Base de datos limpia. Corré POST /api/v1/seed para volver a poblar.");
    }

    private UsuarioEntity crearUsuario(String username, String password, RolUsuario rol, String imagen) {
        UsuarioEntity u = new UsuarioEntity();
        u.setUsername(username);
        u.setPassword(password);
        u.setRol(rol);
        u.setImagenBase64(imagen);
        return u;
    }

    private TurnoEntity crearTurno(LocalDate fecha, String horario, String mensaje,
                                   PacienteEntity paciente, ServicioEntity servicio, EstadoTurno estado) {
        TurnoEntity t = new TurnoEntity();
        t.setFecha(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        t.setHorario(horario);
        t.setMensajeAdicional(mensaje);
        t.setEstado(estado);
        t.setPaciente(paciente);
        t.setServicio(servicio);
        return t;
    }
}
