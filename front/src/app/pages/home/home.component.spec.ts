import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { HomeComponent } from './home.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { AuthService } from '../../services/auth.service';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;
  let navigateSpy: jasmine.Spy;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['getToken']);
    authServiceSpy.getToken.and.returnValue(null);

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [HomeComponent, ButtonComponent],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateSpy = spyOn(router, 'navigate').and.resolveTo(true);

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to feed when a token is present', () => {
    authServiceSpy.getToken.and.returnValue('token');

    component.ngOnInit();

    expect(navigateSpy).toHaveBeenCalledWith(['/feed']);
  });

  it('should stay on home when no token is present', () => {
    authServiceSpy.getToken.and.returnValue(null);

    component.ngOnInit();

    expect(navigateSpy).not.toHaveBeenCalledWith(['/feed']);
  });
});
