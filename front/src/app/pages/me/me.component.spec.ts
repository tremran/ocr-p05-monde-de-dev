import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../services/auth.service';
import { MeService } from '../../services/me.service';
import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let meServiceSpy: jasmine.SpyObj<MeService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    meServiceSpy = jasmine.createSpyObj<MeService>('MeService', ['getMe', 'updateMe']);
    meServiceSpy.getMe.and.returnValue(of({ id: 1, pseudo: 'moi', email: 'me@test.com' }));
    meServiceSpy.updateMe.and.returnValue(of({ id: 1, pseudo: 'after', email: 'after@test.com', token: 'new-token' }));
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['saveToken']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [MeComponent],
      providers: [
        { provide: MeService, useValue: meServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load profile data on init and patch the form', () => {
    fixture.detectChanges();

    expect(meServiceSpy.getMe).toHaveBeenCalledTimes(1);
    expect(component.loadingProfile).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(component.meForm.getRawValue()).toEqual({
      pseudo: 'moi',
      email: 'me@test.com',
      password: '',
    });
  });

  it('should submit profile updates to PUT /me and reset pristine state', () => {
    fixture.detectChanges();

    component.meForm.patchValue({
      pseudo: 'after',
      email: 'after@test.com',
      password: 'StrongPass123',
    });

    component.submit();

    expect(meServiceSpy.updateMe).toHaveBeenCalledOnceWith({
      pseudo: 'after',
      email: 'after@test.com',
      password: 'StrongPass123',
    });
    expect(authServiceSpy.saveToken).toHaveBeenCalledOnceWith('new-token');
    expect(component.savingProfile).toBeFalse();
    expect(component.successMessage).toBe('Vos informations ont ete mises a jour.');
    expect(component.errorMessage).toBe('');
    expect(component.meForm.pristine).toBeTrue();
    expect(component.meForm.getRawValue()).toEqual({
      pseudo: 'after',
      email: 'after@test.com',
      password: '',
    });
  });

  it('should not submit when the form is invalid', () => {
    fixture.detectChanges();

    component.meForm.patchValue({
      pseudo: 'ab',
      email: 'invalid-email',
      password: 'short',
    });

    component.submit();

    expect(meServiceSpy.updateMe).not.toHaveBeenCalled();
    expect(authServiceSpy.saveToken).not.toHaveBeenCalled();
    expect(component.meForm.controls.pseudo.touched).toBeTrue();
    expect(component.meForm.controls.email.touched).toBeTrue();
    expect(component.meForm.controls.password.touched).toBeTrue();
  });

  it('should show an error message when updating profile fails', () => {
    meServiceSpy.updateMe.and.returnValue(throwError(() => new Error('update failed')));
    fixture.detectChanges();

    component.meForm.patchValue({
      pseudo: 'after',
      email: 'after@test.com',
      password: 'StrongPass123',
    });

    component.submit();

    expect(meServiceSpy.updateMe).toHaveBeenCalledTimes(1);
    expect(authServiceSpy.saveToken).not.toHaveBeenCalled();
    expect(component.savingProfile).toBeFalse();
    expect(component.errorMessage).toBe('Impossible de mettre a jour vos informations pour le moment.');
    expect(component.successMessage).toBe('');
  });
});