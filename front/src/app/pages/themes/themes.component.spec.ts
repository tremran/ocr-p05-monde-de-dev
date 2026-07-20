import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, throwError } from 'rxjs';

import { TopicService } from '../../services/topic.service';
import { ThemesComponent } from './themes.component';

describe('ThemesComponent', () => {
  let component: ThemesComponent;
  let fixture: ComponentFixture<ThemesComponent>;
  let topicServiceSpy: jasmine.SpyObj<TopicService>;

  beforeEach(async () => {
    topicServiceSpy = jasmine.createSpyObj<TopicService>('TopicService', [
      'getTopics',
      'subscribeToTopic',
    ]);
    topicServiceSpy.getTopics.and.returnValue(of([]));
    topicServiceSpy.subscribeToTopic.and.returnValue(of({}));

    await TestBed.configureTestingModule({
      declarations: [ThemesComponent],
      providers: [{ provide: TopicService, useValue: topicServiceSpy }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ThemesComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call topic service on init and load topics', () => {
    const apiTopics = [{ id: 1, name: 'Spring', description: 'Topic spring' }];
    topicServiceSpy.getTopics.and.returnValue(of(apiTopics));

    fixture.detectChanges();

    expect(topicServiceSpy.getTopics).toHaveBeenCalledTimes(1);
    expect(component.topics).toEqual(apiTopics);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  it('should set error message when api call fails', () => {
    topicServiceSpy.getTopics.and.returnValue(
      throwError(() => new Error('topic load failed')),
    );

    fixture.detectChanges();

    expect(topicServiceSpy.getTopics).toHaveBeenCalledTimes(1);
    expect(component.topics).toEqual([]);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('Impossible de charger les thèmes pour le moment.');
  });

  it('should call subscribe endpoint and mark topic as registered', () => {
    const topic = { id: 12, name: 'Docker', description: 'Docker', registered: false };

    component.subscribe(topic);

    expect(topicServiceSpy.subscribeToTopic).toHaveBeenCalledOnceWith(12);
    expect(topic.registered).toBeTrue();
    expect(component.isSubscribing(topic)).toBeFalse();
    expect(component.subscribeErrorMessage).toBe('');
  });

  it('should not call subscribe endpoint when topic is already registered', () => {
    const topic = { id: 7, name: 'Spring', description: 'Spring', registered: true };

    component.subscribe(topic);

    expect(topicServiceSpy.subscribeToTopic).not.toHaveBeenCalled();
  });

  it('should set a subscribe error when subscribe endpoint fails', () => {
    const topic = { id: 5, name: 'Kubernetes', description: 'K8s', registered: false };
    topicServiceSpy.subscribeToTopic.and.returnValue(
      throwError(() => new Error('subscription failed')),
    );

    component.subscribe(topic);

    expect(topicServiceSpy.subscribeToTopic).toHaveBeenCalledOnceWith(5);
    expect(topic.registered).toBeFalse();
    expect(component.isSubscribing(topic)).toBeFalse();
    expect(component.subscribeErrorMessage).toBe('L\'abonnement a échoué. Veuillez réessayer.');
  });
});