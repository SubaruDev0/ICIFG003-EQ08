export type RolUsuario = 'ADMIN' | 'USER' | 'GUEST'; // Define los roles permitidos

export interface Usuario {
  id?: number;
  username: string;
  password?: string;
  rol: RolUsuario;          // Actualizado de string a RolUsuario
  imagenBase64?: string;
  fechaCreacion?: string;   // Recibe la fecha del backend
}