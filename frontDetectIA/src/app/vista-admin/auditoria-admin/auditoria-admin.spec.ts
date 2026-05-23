import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditoriaAdmin } from './auditoria-admin';

describe('AuditoriaAdmin', () => {
  let component: AuditoriaAdmin;
  let fixture: ComponentFixture<AuditoriaAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditoriaAdmin],
    }).compileComponents();

    fixture = TestBed.createComponent(AuditoriaAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
