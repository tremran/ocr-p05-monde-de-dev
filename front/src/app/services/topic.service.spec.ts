import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

import { TopicService } from './topic.service';

describe('TopicService', () => {
  let service: TopicService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    });

    service = TestBed.inject(TopicService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call GET /topic with bearer token when a token exists', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.getTopics().subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}topic`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');

    req.flush([]);
  });

  it('should call GET /topic without authorization header when token is absent', () => {
    authServiceSpy.getToken.and.returnValue(null);

    service.getTopics().subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}topic`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Authorization')).toBeFalse();

    req.flush([]);
  });

  it('should normalize response when api returns topics under data field', () => {
    authServiceSpy.getToken.and.returnValue(null);
    const apiTopics = [{ id: 1, name: 'Spring', description: 'Topic spring' }];

    service.getTopics().subscribe((topics) => {
      expect(topics).toEqual(apiTopics);
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}topic`);
    req.flush({ data: apiTopics });
  });

  it('should call POST /subscription/{topicId} with bearer token when subscribing', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.subscribeToTopic(42).subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}subscription/42`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');
    expect(req.request.body).toEqual({});

    req.flush({ topicId: 42, subscribed: true });
  });

  it('should call DELETE /subscription/{topicId} with bearer token when unsubscribing', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.unsubscribeFromTopic(42).subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}subscription/42`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');

    req.flush({ topicId: 42, subscribed: false });
  });
});