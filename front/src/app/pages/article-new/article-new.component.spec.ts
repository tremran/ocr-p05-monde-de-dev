import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { ArticleNewComponent } from './article-new.component';
import { PostService } from '../../services/post.service';
import { TopicService } from '../../services/topic.service';

describe('ArticleNewComponent', () => {
  let component: ArticleNewComponent;
  let fixture: ComponentFixture<ArticleNewComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;
  let topicServiceSpy: jasmine.SpyObj<TopicService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    postServiceSpy = jasmine.createSpyObj<PostService>('PostService', ['createPost']);
    postServiceSpy.createPost.and.returnValue(
      of({ id: 10, title: 'Mon article', content: 'Du contenu' }),
    );

    topicServiceSpy = jasmine.createSpyObj<TopicService>('TopicService', ['getTopics']);
    topicServiceSpy.getTopics.and.returnValue(
      of([{ id: 7, name: 'Angular', description: 'Frontend' }]),
    );

    routerSpy = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [ArticleNewComponent],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: TopicService, useValue: topicServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleNewComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load topics on init', () => {
    fixture.detectChanges();

    expect(topicServiceSpy.getTopics).toHaveBeenCalledTimes(1);
    expect(component.loadingTopics).toBeFalse();
    expect(component.topicErrorMessage).toBe('');
    expect(component.topics.length).toBe(1);
  });

  it('should submit article to POST /post and redirect to feed', () => {
    fixture.detectChanges();

    component.articleForm.setValue({
      topicId: '7',
      title: 'Mon article',
      content: 'Du contenu',
    });

    component.submit();

    expect(postServiceSpy.createPost).toHaveBeenCalledTimes(1);
    const payload = postServiceSpy.createPost.calls.mostRecent().args[0];
    expect(payload.topicId).toBe(7);
    expect(payload.title).toBe('Mon article');
    expect(payload.content).toBe('Du contenu');
    expect(payload.publishedAt).toMatch(/^\d{4}-\d{2}-\d{2}$/);
    expect(component.savingArticle).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(component.successMessage).toBe('Article publié avec succès.');
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(['/feed']);
  });

  it('should not submit when form is invalid', () => {
    fixture.detectChanges();

    component.articleForm.setValue({
      topicId: '',
      title: 'ab',
      content: '',
    });

    component.submit();

    expect(postServiceSpy.createPost).not.toHaveBeenCalled();
    expect(component.articleForm.invalid).toBeTrue();
  });

  it('should show an error when article creation fails', () => {
    postServiceSpy.createPost.and.returnValue(
      throwError(() => new Error('create failed')),
    );
    fixture.detectChanges();

    component.articleForm.setValue({
      topicId: '7',
      title: 'Mon article',
      content: 'Du contenu',
    });

    component.submit();

    expect(postServiceSpy.createPost).toHaveBeenCalledTimes(1);
    expect(component.savingArticle).toBeFalse();
    expect(component.errorMessage).toBe('Impossible de publier l\'article pour le moment.');
  });
});
