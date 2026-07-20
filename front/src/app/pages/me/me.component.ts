import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { MeService } from '../../services/me.service';

@Component({
  selector: 'app-me',
  templateUrl: './me.component.html',
  styleUrls: ['./me.component.scss'],
})
export class MeComponent implements OnInit {
  readonly meForm = this.fb.nonNullable.group({
    pseudo: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.minLength(8)]],
  });

  loadingProfile = false;
  savingProfile = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly meService: MeService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.loadMe();
  }

  loadMe(): void {
    this.loadingProfile = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.meService.getMe().subscribe({
      next: (me) => {
        this.loadingProfile = false;
        this.meForm.patchValue({
          pseudo: me.pseudo ?? '',
          email: me.email ?? '',
          password: '',
        });
      },
      error: () => {
        this.loadingProfile = false;
        this.errorMessage = 'Impossible de charger vos informations pour le moment.';
      },
    });
  }

  submit(): void {
    if (this.meForm.invalid || this.loadingProfile || this.savingProfile) {
      this.meForm.markAllAsTouched();
      return;
    }

    this.savingProfile = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.meForm.getRawValue();
    this.meService
      .updateMe({
        pseudo: formValue.pseudo,
        email: formValue.email,
        password: formValue.password,
      })
      .subscribe({
        next: (me) => {
          this.savingProfile = false;
          if (me.token) {
            this.authService.saveToken(me.token);
          }
          this.successMessage = 'Vos informations ont ete mises a jour.';
          this.meForm.patchValue({
            pseudo: me.pseudo ?? formValue.pseudo,
            email: me.email ?? formValue.email,
            password: '',
          });
          this.meForm.markAsPristine();
          this.meForm.markAsUntouched();
        },
        error: () => {
          this.savingProfile = false;
          this.errorMessage = 'Impossible de mettre a jour vos informations pour le moment.';
        },
      });
  }
}
