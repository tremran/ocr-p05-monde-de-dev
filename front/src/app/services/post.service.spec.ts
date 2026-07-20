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

  it('should call GET /post/{postId}/comment with bearer token when a token exists', (done) => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.getComments(12).subscribe((comments) => {
      expect(comments.length).toBe(1);
      expect(comments[0].content).toBe('Nice article');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}post/12/comment`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');

    req.flush([
      {
        id: 3,
        content: 'Nice article',
        author: { email: 'commenter@test.com', pseudo: 'commenter' },
      },
    ]);
  });

  it('should call POST /post/{postId}/comment with payload and bearer token', (done) => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    service.addComment(12, 'Nouveau commentaire').subscribe((comment) => {
      expect(comment.content).toBe('Nouveau commentaire');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}post/12/comment`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');
    expect(req.request.body).toEqual({ content: 'Nouveau commentaire' });

    req.flush({
      id: 10,
      content: 'Nouveau commentaire',
      author: { email: 'commenter@test.com', pseudo: 'commenter' },
    });
  });

  it('should call POST /post with payload and bearer token', (done) => {
    authServiceSpy.getToken.and.returnValue('fake-token');

    const payload = {
      topicId: 7,
      title: 'Nouveau post',
      content: 'Contenu du post',
      publishedAt: '2026-07-20',
    };

    service.createPost(payload).subscribe((post) => {
      expect(post.title).toBe('Nouveau post');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}post`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');
    expect(req.request.body).toEqual(payload);

    req.flush({
      id: 42,
      title: 'Nouveau post',
      content: 'Contenu du post',
      author: { email: 'author@test.com', pseudo: 'author' },
    });
  });
});