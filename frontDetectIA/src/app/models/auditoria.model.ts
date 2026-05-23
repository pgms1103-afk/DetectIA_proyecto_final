export interface AuditoriaLogModel {
  id: number;
  correo: string;
  nombreUsuario: string;
  accion: string;
  modulo: string;
  descripcion: string;
  ip: string;
  navegador: string;
  datoAnterior: string;
  datoNuevo: string;
  recurso: string;
  idRecurso: string;
  exitoso: boolean;
  fecha: string;
}
