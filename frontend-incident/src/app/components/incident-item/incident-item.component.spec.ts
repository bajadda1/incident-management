import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IncidentItemComponent } from './incident-item.component';

describe('IncidentItemComponent', () => {
  let component: IncidentItemComponent;
  let fixture: ComponentFixture<IncidentItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [IncidentItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IncidentItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
