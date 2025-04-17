import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ZoneComponent} from './zone.component';

describe('DashboardComponent', () => {
  let component: ZoneComponent;
  let fixture: ComponentFixture<ZoneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ZoneComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ZoneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
