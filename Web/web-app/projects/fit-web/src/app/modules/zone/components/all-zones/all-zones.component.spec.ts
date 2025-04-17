import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AllZonesComponent} from './all-zones.component';

describe('MainContainerComponent', () => {
  let component: AllZonesComponent;
  let fixture: ComponentFixture<AllZonesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AllZonesComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AllZonesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
