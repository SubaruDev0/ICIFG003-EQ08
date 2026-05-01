import { Usuario } from './usuario.model';

export interface Paciente {
  id?: number;
  nombreCompleto: string;
  rut: string;
  telefono: string;
  email: string;
  usuario?: Usuario | null;
}
