import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VistaAdmin } from './vista-admin';

describe('VistaAdmin', () => {
  let component: VistaAdmin;
  let fixture: ComponentFixture<VistaAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VistaAdmin],
    }).compileComponents();

    fixture = TestBed.createComponent(VistaAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
