import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { FeedComponent } from './feed.component';
import { FeedService } from '../../services/feed.service';

describe('FeedComponent', () => {
  let component: FeedComponent;
  let fixture: ComponentFixture<FeedComponent>;
  let feedServiceSpy: jasmine.SpyObj<FeedService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    feedServiceSpy = jasmine.createSpyObj<FeedService>('FeedService', ['getFeed']);
    feedServiceSpy.getFeed.and.returnValue(of([]));
    routerSpy = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [FeedComponent],
      providers: [
        { provide: FeedService, useValue: feedServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(FeedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load articles on init', () => {
    feedServiceSpy.getFeed.and.returnValue(
      of([
        {
          id: 1,
          title: 'Article 1',
          content: 'Contenu',
        },
      ]),
    );

    component.loadFeed();

    expect(feedServiceSpy.getFeed).toHaveBeenCalledWith('DESC');
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(component.articles.length).toBe(1);
  });

  it('should set an error message when feed loading fails', () => {
    feedServiceSpy.getFeed.and.returnValue(
      throwError(() => new Error('feed failed')),
    );

    component.loadFeed();

    expect(feedServiceSpy.getFeed).toHaveBeenCalledWith('DESC');
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe(
      'Impossible de charger le feed pour le moment.',
    );
  });

  it('should navigate to /article/nouveau', () => {
    component.goToNewArticle();

    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(['/article/nouveau']);
  });

  it('should toggle sort and reload feed with ASC sort', () => {
    component.toggleSortByPublishedAt();

    expect(component.selectedSort).toBe('ASC');
    expect(feedServiceSpy.getFeed).toHaveBeenCalledWith('ASC');
  });

  it('should expose down arrow when current sort is DESC', () => {
    component.selectedSort = 'DESC';

    expect(component.sortArrow).toBe('↓');
  });

  it('should expose up arrow when current sort is ASC', () => {
    component.selectedSort = 'ASC';

    expect(component.sortArrow).toBe('↑');
  });
});