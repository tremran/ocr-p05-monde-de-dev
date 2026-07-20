import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';

import { PostService } from '../../services/post.service';
import { ArticleComponent } from './article.component';

describe('ArticleComponent', () => {
  let component: ArticleComponent;
  let fixture: ComponentFixture<ArticleComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;

  beforeEach(async () => {
    postServiceSpy = jasmine.createSpyObj<PostService>('PostService', ['getPost', 'getComments']);
    postServiceSpy.getPost.and.returnValue(
      of([
        {
          id: 12,
          title: 'Post Title',
          content: 'Post content',
          publishedAt: '2026-07-20',
          topic: { id: 7, name: 'Angular', description: 'Angular topic' },
          author: { email: 'author@test.com', pseudo: 'author' },
        },
      ]),
    );
    postServiceSpy.getComments.and.returnValue(
      of([
        {
          id: 99,
          content: 'Premier commentaire',
          createdAt: '2026-07-20',
          author: { email: 'commenter@test.com', pseudo: 'commenter' },
        },
      ]),
    );

    await TestBed.configureTestingModule({
      declarations: [ArticleComponent],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => (key === 'id_article' ? '12' : null),
              },
            },
          },
        },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the article from /post/{id_article} on init', () => {
    fixture.detectChanges();

    expect(postServiceSpy.getPost).toHaveBeenCalledOnceWith('12');
    expect(postServiceSpy.getComments).toHaveBeenCalledOnceWith('12');
    expect(component.loading).toBeFalse();
    expect(component.commentsLoading).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(component.commentsErrorMessage).toBe('');
    expect(component.articleId).toBe('12');
    expect(component.articles.length).toBe(1);
    expect(component.articles[0].title).toBe('Post Title');
    expect(component.articles[0].topic?.name).toBe('Angular');
    expect(component.comments.length).toBe(1);
    expect(component.comments[0].content).toBe('Premier commentaire');
  });

  it('should show an error when article loading fails', () => {
    postServiceSpy.getPost.and.returnValue(throwError(() => new Error('load failed')));
    postServiceSpy.getComments.and.returnValue(of([]));

    fixture.detectChanges();

    expect(postServiceSpy.getPost).toHaveBeenCalledOnceWith('12');
    expect(component.loading).toBeFalse();
    expect(component.articles).toEqual([]);
    expect(component.errorMessage).toBe('Impossible de charger l\'article pour le moment.');
  });

  it('should show an error when comments loading fails', () => {
    postServiceSpy.getComments.and.returnValue(throwError(() => new Error('load comments failed')));

    fixture.detectChanges();

    expect(postServiceSpy.getComments).toHaveBeenCalledOnceWith('12');
    expect(component.commentsLoading).toBeFalse();
    expect(component.comments).toEqual([]);
    expect(component.commentsErrorMessage).toBe('Impossible de charger les commentaires pour le moment.');
  });
});