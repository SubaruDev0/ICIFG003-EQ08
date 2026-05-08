package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.entity.PacienteEntity;
import com.example.demo.entity.ProfesionalEntity;
import com.example.demo.entity.ServicioEntity;
import com.example.demo.entity.TurnoEntity;
import com.example.demo.entity.UsuarioEntity;
import com.example.demo.entity.enums.EstadoTurno;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.repository.ProfesionalRepository;
import com.example.demo.repository.ServicioRepository;
import com.example.demo.repository.TurnoRepository;
import com.example.demo.entity.enums.RolUsuario;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final PacienteRepository pacienteRepository;
    private final TurnoRepository turnoRepository;

    @Value("${APP_ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${APP_ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    @Value("${APP_ADMIN_ROLE:ADMIN}")
    private String adminRole;

    @Value("${APP_FORCE_TEST_DATA:false}")
    private boolean forceTestData;

    private static final String AVATAR_BASE64 =
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgQw3x4sAAAAASUVORK5CYII=";

    public DataInitializer(
        UsuarioRepository usuarioRepository,
        ServicioRepository servicioRepository,
        ProfesionalRepository profesionalRepository,
        PacienteRepository pacienteRepository,
        TurnoRepository turnoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.profesionalRepository = profesionalRepository;
        this.pacienteRepository = pacienteRepository;
        this.turnoRepository = turnoRepository;
    }

    @Override
    public void run(String... args) {
        if (!forceTestData) return;

        turnoRepository.deleteAll();
        pacienteRepository.deleteAll();
        profesionalRepository.deleteAll();
        servicioRepository.deleteAll();
        usuarioRepository.deleteAll();

        ServicioEntity odontologia = servicioRepository.save(new ServicioEntity(null, "Odontologia General", AVATAR_BASE64));
        ServicioEntity ortodoncia = servicioRepository.save(new ServicioEntity(null, "Ortodoncia", AVATAR_BASE64));
        ServicioEntity implantes = servicioRepository.save(new ServicioEntity(null, "Implantes Dentales", AVATAR_BASE64));
        ServicioEntity endodoncia = servicioRepository.save(new ServicioEntity(null, "Endodoncia", AVATAR_BASE64));
        ServicioEntity blanqueamiento = servicioRepository.save(new ServicioEntity(null, "Blanqueamiento", AVATAR_BASE64));

        UsuarioEntity admin = new UsuarioEntity();
        admin.setUsername(adminUsername);
        admin.setPassword(adminPassword);
        admin.setRol(RolUsuario.PROFESIONAL);
        admin.setImagenBase64(AVATAR_BASE64);
        admin = usuarioRepository.save(admin);

        UsuarioEntity profesional2User = new UsuarioEntity();
        profesional2User.setUsername("profesional2");
        profesional2User.setPassword("profesional123");
        profesional2User.setRol(RolUsuario.PROFESIONAL);
        profesional2User.setImagenBase64(AVATAR_BASE64);
        profesional2User = usuarioRepository.save(profesional2User);

        UsuarioEntity pacienteUser = new UsuarioEntity();
        pacienteUser.setUsername("paciente1");
        pacienteUser.setPassword("paciente123");
        pacienteUser.setRol(RolUsuario.PACIENTE);
        pacienteUser.setImagenBase64(AVATAR_BASE64);
        pacienteUser = usuarioRepository.save(pacienteUser);

        profesionalRepository.save(new ProfesionalEntity(null, "Profesional 1", AVATAR_BASE64, ortodoncia));
        profesionalRepository.save(new ProfesionalEntity(null, "Profesional 2", AVATAR_BASE64, implantes));
        profesionalRepository.save(new ProfesionalEntity(null, "Profesional 3", AVATAR_BASE64, endodoncia));

        PacienteEntity paciente1 = new PacienteEntity();
        paciente1.setNombreCompleto("Paciente 1");
        paciente1.setRut("12345678-9");
        paciente1.setTelefono("+56911111111");
        paciente1.setEmail("paciente1@clinica.cl");
        paciente1.setUsuario(pacienteUser);
        paciente1 = pacienteRepository.save(paciente1);

        PacienteEntity paciente2 = new PacienteEntity();
        paciente2.setNombreCompleto("Paciente 2");
        paciente2.setRut("98765432-1");
        paciente2.setTelefono("+56922222222");
        paciente2.setEmail("paciente2@clinica.cl");
        paciente2 = pacienteRepository.save(paciente2);

        turnoRepository.save(crearTurno(LocalDate.now().plusDays(2), "10:00", "Control inicial", paciente1, ortodoncia));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(3), "11:30", "Molestia al masticar", paciente2, odontologia));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(4), "15:00", "Evaluacion implantes", paciente1, implantes));
        turnoRepository.save(crearTurno(LocalDate.now().plusDays(5), "16:30", "Blanqueamiento estético", paciente2, blanqueamiento));
    }

    private TurnoEntity crearTurno(
        LocalDate fecha,
        String horario,
        String mensaje,
        PacienteEntity paciente,
        ServicioEntity servicio
    ) {
        TurnoEntity turno = new TurnoEntity();
        turno.setFecha(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        turno.setHorario(horario);
        turno.setMensajeAdicional(mensaje);
        turno.setEstado(EstadoTurno.CONFIRMADO);
        turno.setPaciente(paciente);
        turno.setServicio(servicio);
        return turno;
    }
}
