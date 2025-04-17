import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddMembersComponent} from './add-members.component';

describe('MainContainerComponent', () => {
  let component: AddMembersComponent;
  let fixture: ComponentFixture<AddMembersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddMembersComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AddMembersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
