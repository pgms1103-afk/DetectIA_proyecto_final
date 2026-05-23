import { ResultadoIAModel } from './resultadoIA.model';

export interface AnalisisModel{
  id?: number;
  porcentajeFinal: number;
  veredicto: string;
  iaRecomendada: string;
  fechaAnalisis: string;
  archivoId: number;
  nombreArchivo: string;
  resultadosIAs?: ResultadoIAModel[];
}
