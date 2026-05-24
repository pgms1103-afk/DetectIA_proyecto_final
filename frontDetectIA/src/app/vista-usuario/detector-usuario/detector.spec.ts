import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Detector } from './detector';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';
import { ArchivoService } from '../../services/archivo.service';
import { ResultadoIAService } from '../../services/resultadoIA.service';
import { Subject, of } from 'rxjs';
import { ArchivoModel } from '../../models/archivo.model';

// ─── Stubs ───────────────────────────────────────────────────
const archivoSeleccionadoSubject = new Subject<ArchivoModel>();

const archivoServiceStub = {
  archivoSeleccionado$: archivoSeleccionadoSubject.asObservable(),
  analisisCompletado$: { next: jasmine.createSpy('next') },
  postAnalizarArchivo: jasmine.createSpy('postAnalizarArchivo').and.returnValue(of({})),
  postAnalizarUrl:     jasmine.createSpy('postAnalizarUrl').and.returnValue(of({})),
  postAnalizarTexto:   jasmine.createSpy('postAnalizarTexto').and.returnValue(of({})),
};

const resultadoServiceStub = {
  getMostrarResultadosPorId: jasmine.createSpy('getMostrarResultadosPorId').and.returnValue(of([])),
  getMostrarAnalisisPorId:   jasmine.createSpy('getMostrarAnalisisPorId').and.returnValue(of([])),
};

// ─── Suite ───────────────────────────────────────────────────
describe('Detector', () => {

  let component: Detector;
  let fixture: ComponentFixture<Detector>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Detector],
      providers: [
        provideToastr(),
        provideAnimations(),
        { provide: ArchivoService,     useValue: archivoServiceStub },
        { provide: ResultadoIAService, useValue: resultadoServiceStub },
      ]
    }).compileComponents();

    fixture   = TestBed.createComponent(Detector);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Verifica que el componente se cree correctamente.
   */
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Verifica que el tipo de herramienta por defecto sea texto.
   */
  it('should initialize tipoHerramienta as texto', () => {
    expect(component.tipoHerramienta).toBe('texto');
  });

  /**
   * Verifica que la pestaña inicial sea text.
   */
  it('should initialize activeTab as text', () => {
    expect(component.activeTab).toBe('text');
  });

  /**
   * Verifica el cambio de pestaña.
   */
  it('should switch tabs correctly', () => {
    component.switchTab('file');
    expect(component.activeTab).toBe('file');
  });

  /**
   * Verifica que se elimine el archivo local.
   */
  it('should remove selected file', () => {
    component.archivo = new File(['contenido'], 'test.txt');

    const event = new Event('click');
    spyOn(event, 'stopPropagation');

    component.quitarArchivo(event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(component.archivo).toBeNull();
  });

  /**
   * Verifica que se elimine el archivo histórico.
   */
  it('should remove historial file', () => {
    const event = new Event('click');
    spyOn(event, 'stopPropagation');

    component.quitarArchivoHistorico(event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(component.archivoHistorico).toBeNull();
  });

  /**
   * Verifica la detección de categoría imagen.
   */
  it('should detect image category', () => {
    const categoria = component.determinarCategoriaPorArchivo('foto.png');
    expect(categoria).toBe('imagen');
  });

  /**
   * Verifica que detecte video correctamente.
   */
  it('should detect video category', () => {
    const categoria = component.determinarCategoriaPorArchivo('video.mp4');
    expect(categoria).toBe('video');
  });

  /**
   * Verifica que detecte audio correctamente.
   */
  it('should detect audio category', () => {
    const categoria = component.determinarCategoriaPorArchivo('audio.mp3');
    expect(categoria).toBe('audio');
  });

  /**
   * Verifica que detecte texto correctamente.
   */
  it('should detect text category', () => {
    const categoria = component.determinarCategoriaPorArchivo('documento.pdf');
    expect(categoria).toBe('texto');
  });

});
