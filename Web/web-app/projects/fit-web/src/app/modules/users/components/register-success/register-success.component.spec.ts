import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RegisterSuccessComponent} from './register-success.component';

describe('AuthComponentComponent', () => {
  let component: RegisterSuccessComponent;
  let fixture: ComponentFixture<RegisterSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterSuccessComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
