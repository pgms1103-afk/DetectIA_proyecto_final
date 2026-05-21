import { Role } from './role.enum';

export interface UsuarioModel{
  id?: number;
  nombreUsuario: string;
  correo: string;
  contrasena?: string;
  role: Role;


}
