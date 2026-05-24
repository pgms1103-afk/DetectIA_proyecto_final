import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Topbar } from './topbar';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';

describe('Topbar', () => {

  let component: Topbar;
  let fixture: ComponentFixture<Topbar>;

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [Topbar],
      providers: [
        provideToastr(),
        provideAnimations()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Topbar);

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
   * Verifica el estado inicial del menú de perfil.
   */
  it('should initialize profile menu as closed', () => {
    expect(component.showProfileMenu).toBeFalse();
  });

  /**
   * Verifica que el menú de perfil cambie de estado.
   */
  it('should toggle profile menu', () => {

    const estadoInicial = component.showProfileMenu;

    component.toggleProfileMenu();

    expect(component.showProfileMenu)
      .toBe(!estadoInicial);
  });

  /**
   * Verifica que el usuario tenga valores iniciales.
   */
  it('should initialize user data', () => {

    expect(component.user.name)
      .toBe('cargando...');

    expect(component.user.email)
      .toBe('cargando...');

    expect(component.user.files)
      .toBe(0);
  });

  /**
   * Verifica que el evento menuToggle sea emitido.
   */
  it('should emit menuToggle event', () => {

    spyOn(component.menuToggle, 'emit');

    component.abrirMenu();

    expect(component.menuToggle.emit)
      .toHaveBeenCalled();
  });
});
