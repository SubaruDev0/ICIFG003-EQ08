import { Usuario } from '../../auth/models/usuario.model';

export interface Paciente {
  id?: number;
  nombreCompleto: string;
  rut: string;
  telefono: string;
  email: string;
  usuario?: Usuario | null;
  fechaCreacion?: string;     // Recibe la fecha del backend
}
