import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UpdateZoneDialogComponent} from './update-zone-dialog.component';

describe('MainContainerComponent', () => {
  let component: UpdateZoneDialogComponent;
  let fixture: ComponentFixture<UpdateZoneDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UpdateZoneDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(UpdateZoneDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
