import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardAdmin } from './dashboard-admin';
import { UsuarioService } from '../../services/usuario.service';
import { AuditoriaService } from '../../services/auditoria.service';
import { ArchivoService } from '../../services/archivo.service';
import { ResultadoIAService } from '../../services/resultadoIA.service';
import { of } from 'rxjs';
import { ArchivoModel } from '../../models/archivo.model';

interface GraficoData {
  labels: string[];
  data: number[];
}

describe('DashboardAdmin', () => {
  let component: DashboardAdmin;
  let fixture: ComponentFixture<DashboardAdmin>;

  const mockUsuarioService = jasmine.createSpyObj('UsuarioService', ['getMostrarUsuarios']);
  const mockAuditoriaService = jasmine.createSpyObj('AuditoriaService', ['getTodos']);
  const mockArchivoService = jasmine.createSpyObj('ArchivoService', ['getAllArchivos']);
  const mockResultadoService = jasmine.createSpyObj('ResultadoIAService', ['getAllResultados']);
  interface DashboardAdminPrivate {
    calcularTiposArchivo(archivos: Partial<ArchivoModel>[]): GraficoData;
    calcularVeredictos(resultados: { porcentajeIA: number }[]): GraficoData;
  }
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardAdmin],
      providers: [
        { provide: UsuarioService, useValue: mockUsuarioService },
        { provide: AuditoriaService, useValue: mockAuditoriaService },
        { provide: ArchivoService, useValue: mockArchivoService },
        { provide: ResultadoIAService, useValue: mockResultadoService }
      ]
    }).compileComponents();

    mockUsuarioService.getMostrarUsuarios.and.returnValue(of([]));
    mockAuditoriaService.getTodos.and.returnValue(of([]));
    mockArchivoService.getAllArchivos.and.returnValue(of([]));
    mockResultadoService.getAllResultados.and.returnValue(of([]));

    fixture = TestBed.createComponent(DashboardAdmin);
    component = fixture.componentInstance;
  });

  it('debería crearse el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería ejecutar cargarDatos en ngAfterViewInit', () => {
    spyOn(component, 'cargarDatos');
    component.ngAfterViewInit();
    expect(component.cargarDatos).toHaveBeenCalled();
  });

  it('debería clasificar correctamente los archivos por tipo', () => {
    const archivos: Partial<ArchivoModel>[] = [
      { rutaAlmacenamiento: 'test.jpg' },
      { rutaAlmacenamiento: 'video.mp4' }
    ];
    const resultado = (component as unknown as DashboardAdminPrivate)
      .calcularTiposArchivo(archivos);

    expect(resultado.labels).toContain('Imagen');
    expect(resultado.labels).toContain('Video');
  });

  it('debería clasificar veredictos según porcentaje IA', () => {
    const resultados = [{ porcentajeIA: 60 }, { porcentajeIA: 30 }];
    const resultado = (component as unknown as DashboardAdminPrivate)
      .calcularVeredictos(resultados);

    expect(resultado.data).toEqual([1, 1]);
  });
})
