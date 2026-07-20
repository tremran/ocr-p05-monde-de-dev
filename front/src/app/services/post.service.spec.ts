import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { PostService } from './post.service';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call GET /post/{postId} with bearer token when a token exists', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.getPost(12).subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}post/12`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');

    req.flush({
      id: 12,
      title: 'Post Title',
      content: 'Post content',
      author: { email: 'author@test.com', pseudo: 'author' },
      topic: { id: 7, name: 'Angular', description: 'Angular topic' },
    });
  });

  it('should normalize a direct post response into an array', (done) => {
    authServiceSpy.getToken.and.returnValue(null);

    service.getPost(12).subscribe((articles) => {
      expect(articles.length).toBe(1);
      expect(articles[0].title).toBe('Post Title');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}post/12`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Authorization')).toBeFalse();

    req.flush({
      id: 12,
      title: 'Post Title',
      content: 'Post content',
      author: { email: 'author@test.com', pseudo: 'author' },
      topic: { id: 7, name: 'Angular', description: 'Angular topic' },
    });
  });
});