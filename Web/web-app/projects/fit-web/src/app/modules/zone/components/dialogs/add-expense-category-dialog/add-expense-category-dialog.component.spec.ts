import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddExpenseCategoryDialogComponent} from './add-expense-category-dialog.component';

describe('MainContainerComponent', () => {
  let component: AddExpenseCategoryDialogComponent;
  let fixture: ComponentFixture<AddExpenseCategoryDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddExpenseCategoryDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AddExpenseCategoryDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
