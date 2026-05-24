import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Sidebar } from './sidebar';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';
import { ArchivoModel } from '../../models/archivo.model';

describe('Sidebar', () => {

  let component: Sidebar;
  let fixture: ComponentFixture<Sidebar>;

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [Sidebar],
      providers: [
        provideToastr(),
        provideAnimations()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Sidebar);

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
   * Verifica que la herramienta inicial sea texto.
   */
  it('should initialize herramientaActual as texto', () => {
    expect(component.herramientaActual).toBe('texto');
  });

  /**
   * Verifica el cambio de herramienta.
   */
  it('should change selected tool', () => {

    component.seleccionarHerramienta('imagen');

    expect(component.herramientaActual).toBe('imagen');
  });

  /**
   * Verifica que el sidebar cambie su estado colapsado.
   */
  it('should toggle sidebar state', () => {

    const estadoInicial = component.isCollapsed;

    component.toggleSidebar();

    expect(component.isCollapsed).toBe(!estadoInicial);
  });

  /**
   * Verifica que el menú móvil cambie su estado.
   */
  it('should toggle mobile menu', () => {

    const estadoInicial = component.mobileOpen;

    component.toggleMobileMenu();

    expect(component.mobileOpen).toBe(!estadoInicial);
  });

  /**
   * Verifica que el menú de perfil cambie de estado.
   */
  it('should toggle profile menu', () => {

    const estadoInicial = component.showProfileMenu;

    component.toggleProfileMenu();

    expect(component.showProfileMenu).toBe(!estadoInicial);
  });

  /**
   * Verifica que el modal de edición se abra.
   */
  it('should open edit modal', () => {

    const archivoMock = {
      id: 1,
      nombre: 'archivo.txt'
    } as ArchivoModel;

    component.abrirModalEditar(archivoMock);

    expect(component.mostrarModalEditar).toBeTrue();

    expect(component.nombreEditando)
      .toBe('archivo.txt');
  });

  /**
   * Verifica que el modal se cierre correctamente.
   */
  it('should close edit modal', () => {

    component.mostrarModalEditar = true;
    component.nombreEditando = 'archivo';

    component.cerrarModalEditar();

    expect(component.mostrarModalEditar).toBeFalse();

    expect(component.nombreEditando).toBe('');
  });

  /**
   * Verifica que el historial inicie vacío.
   */
  it('should initialize historial as empty array', () => {
    expect(component.historialArchivos).toEqual([]);
  });

  /**
   * Verifica que el nombre del archivo inicie vacío.
   */
  it('should initialize nombreArchivo empty', () => {
    expect(component.nombreArchivo).toBe('');
  });
});
