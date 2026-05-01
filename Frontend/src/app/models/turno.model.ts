import { Paciente } from './paciente.model';
import { Servicio } from './servicio.model';

export interface Turno {
  id?: number;
  fecha: string;
  horario: string;
  mensajeAdicional: string;
  paciente: Paciente;
  servicio: Servicio;
}
