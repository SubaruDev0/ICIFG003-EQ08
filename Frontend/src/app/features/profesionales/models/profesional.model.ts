import { Servicio } from '../../servicios/models/servicio.model';

export interface Profesional {
  id?: number;
  nombreCompleto: string;
  imagenBase64: string;
  servicio?: Servicio | null;
}
