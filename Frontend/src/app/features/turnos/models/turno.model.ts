import { Paciente } from '../../pacientes/models/paciente.model';
import { Servicio } from '../../servicios/models/servicio.model';

export interface Turno {
  id?: number;
  fecha: string;
  horario: string;
  mensajeAdicional: string;
  paciente: Paciente;
  servicio: Servicio;
}
