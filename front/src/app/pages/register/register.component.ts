import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

const PASSWORD_RULES = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[=+_\-$#!?]).{9,}$/;

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  readonly registerForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.pattern(PASSWORD_RULES)]],
  });

  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
  ) {}

  submit(): void {
    if (this.registerForm.invalid || this.loading) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.registerForm.getRawValue();
    this.authService
      .register({
        pseudo: formValue.name,
        email: formValue.email,
        password: formValue.password,
      })
      .subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Registration successful.';
        this.registerForm.reset();
      },
      error: () => {
        this.loading = false;
        this.errorMessage =
          'Registration failed. Please verify your data and try again.';
      },
    });
  }
}
