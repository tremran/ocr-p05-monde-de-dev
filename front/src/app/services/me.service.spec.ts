import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { MeService } from './me.service';

describe('MeService', () => {
  let service: MeService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    });

    service = TestBed.inject(MeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call GET /me with bearer token when a token exists', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.getMe().subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}me`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');

    req.flush({ id: 1, email: 'me@test.com', pseudo: 'moi' });
  });

  it('should call GET /me without authorization header when token is absent', () => {
    authServiceSpy.getToken.and.returnValue(null);

    service.getMe().subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}me`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Authorization')).toBeFalse();

    req.flush({ id: 1, email: 'me@test.com', pseudo: 'moi' });
  });

  it('should call PUT /me with bearer token when updating profile', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.updateMe({ email: 'after@test.com', pseudo: 'after', password: 'StrongPass123' }).subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}me`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');
    expect(req.request.body).toEqual({
      email: 'after@test.com',
      pseudo: 'after',
      password: 'StrongPass123',
    });

    req.flush({ id: 1, email: 'after@test.com', pseudo: 'after' });
  });
});