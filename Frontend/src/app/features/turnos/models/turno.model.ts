import { Paciente } from '../../pacientes/models/paciente.model';
import { Servicio } from '../../servicios/models/servicio.model';

export type HorarioTurno = 'MANANA' | 'TARDE';
export type EstadoTurno = 'PENDIENTE' | 'CONFIRMADO' | 'CANCELADO' | 'COMPLETADO';

export interface Turno {
  id?: number;
  fecha: string;
  horario: HorarioTurno;      // Actualizado de string a HorarioTurno
  estado?: EstadoTurno;       // Agregado el estado opcional
  mensajeAdicional: string;
  paciente: Paciente;
  servicio: Servicio;
  fechaCreacion?: string;     // Recibe la fecha del backend
}

