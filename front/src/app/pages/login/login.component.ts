import { Component, DestroyRef } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService, LoginResponse } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    standalone: false
})
export class LoginComponent {
  readonly loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly destroyRef: DestroyRef,
  ) {}

  submit(): void {
    if (this.loginForm.invalid || this.loading) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.loginForm.getRawValue();
    this.authService
      .login({
        email: formValue.email,
        password: formValue.password,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: LoginResponse) => {
          const hasToken = this.authService.saveTokenFromLoginResponse(response);
          this.loading = false;

          if (!hasToken) {
            this.errorMessage = 'Login response did not include a token.';
            return;
          }

          this.successMessage = 'Login successful.';
          this.loginForm.reset();
          this.router.navigate(['/feed']);
        },
        error: () => {
          this.loading = false;
          this.errorMessage = 'Login failed. Please verify your data and try again.';
        },
      });
  }
}