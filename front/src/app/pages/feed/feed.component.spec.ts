import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, throwError } from 'rxjs';

import { FeedComponent } from './feed.component';
import { FeedService } from '../../services/feed.service';

describe('FeedComponent', () => {
  let component: FeedComponent;
  let fixture: ComponentFixture<FeedComponent>;
  let feedServiceSpy: jasmine.SpyObj<FeedService>;

  beforeEach(async () => {
    feedServiceSpy = jasmine.createSpyObj<FeedService>('FeedService', ['getFeed']);
    feedServiceSpy.getFeed.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      declarations: [FeedComponent],
      providers: [{ provide: FeedService, useValue: feedServiceSpy }],
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

    expect(feedServiceSpy.getFeed).toHaveBeenCalled();
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(component.articles.length).toBe(1);
  });

  it('should set an error message when feed loading fails', () => {
    feedServiceSpy.getFeed.and.returnValue(
      throwError(() => new Error('feed failed')),
    );

    component.loadFeed();

    expect(feedServiceSpy.getFeed).toHaveBeenCalled();
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe(
      'Impossible de charger le feed pour le moment.',
    );
  });
});