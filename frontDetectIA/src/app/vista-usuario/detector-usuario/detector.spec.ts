import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Detector } from './detector';

describe('Detector', () => {
  let component: Detector;
  let fixture: ComponentFixture<Detector>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Detector],
    }).compileComponents();

    fixture = TestBed.createComponent(Detector);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
