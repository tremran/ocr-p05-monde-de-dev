import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { FeedService } from './feed.service';

describe('FeedService', () => {
  let service: FeedService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    });

    service = TestBed.inject(FeedService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call GET /feed with sort=DESC by default', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.getFeed().subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}feed?sort=DESC`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');

    req.flush([]);
  });

  it('should call GET /feed with sort=ASC when requested', () => {
    authServiceSpy.getToken.and.returnValue(null);

    service.getFeed('ASC').subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}feed?sort=ASC`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Authorization')).toBeFalse();

    req.flush([]);
  });
});
