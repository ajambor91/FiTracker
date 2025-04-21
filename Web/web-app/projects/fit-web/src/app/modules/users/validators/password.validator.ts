import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';


export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value: string = control.value; // Pobierz wartość kontrolki
    if (!value) {
      return null;
    }
    const errors: ValidationErrors = {};
    const minLength = 8;
    if (value.length < minLength) {
      errors['minLength'] = {requiredLength: minLength, actualLength: value.length};
    }
    const hasUppercase = /[A-Z]/.test(value);
    if (!hasUppercase) {
      errors['uppercase'] = true;
    }
    const hasLowercase = /[a-z]/.test(value);
    if (!hasLowercase) {
      errors['lowercase'] = true;
    }
    const hasSpecial = /[^\w\s]/.test(value);
    if (!hasSpecial) {
      errors['specialChar'] = true;
    }
    return Object.keys(errors).length > 0 ? errors : null;
  };
}
