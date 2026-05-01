import { Servicio } from './servicio.model';

export interface Profesional {
  id?: number;
  nombreCompleto: string;
  imagenBase64: string;
  servicio: Servicio;
}
