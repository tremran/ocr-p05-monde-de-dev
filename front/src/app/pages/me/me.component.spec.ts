import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../services/auth.service';
import { MeService } from '../../services/me.service';
import { TopicService } from '../../services/topic.service';
import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let meServiceSpy: jasmine.SpyObj<MeService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let topicServiceSpy: jasmine.SpyObj<TopicService>;

  beforeEach(async () => {
    meServiceSpy = jasmine.createSpyObj<MeService>('MeService', ['getMe', 'updateMe']);
    meServiceSpy.getMe.and.returnValue(of({ id: 1, pseudo: 'moi', email: 'me@test.com' }));
    meServiceSpy.updateMe.and.returnValue(of({ id: 1, pseudo: 'after', email: 'after@test.com', token: 'new-token' }));
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['saveToken']);
    topicServiceSpy = jasmine.createSpyObj<TopicService>('TopicService', ['getTopics', 'unsubscribeFromTopic']);
    topicServiceSpy.getTopics.and.returnValue(
      of([
        { id: 10, name: 'Spring', description: 'Spring topic', registered: true },
        { id: 11, name: 'Docker', description: 'Docker topic', registered: false },
      ]),
    );
    topicServiceSpy.unsubscribeFromTopic.and.returnValue(of({}));

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [MeComponent],
      providers: [
        { provide: MeService, useValue: meServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: TopicService, useValue: topicServiceSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load profile data on init and patch the form', () => {
    fixture.detectChanges();

    expect(meServiceSpy.getMe).toHaveBeenCalledTimes(1);
    expect(topicServiceSpy.getTopics).toHaveBeenCalledTimes(1);
    expect(component.loadingProfile).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(component.loadingSubscribedTopics).toBeFalse();
    expect(component.subscribedTopics.length).toBe(1);
    expect(component.subscribedTopics[0].name).toBe('Spring');
    expect(component.meForm.getRawValue()).toEqual({
      pseudo: 'moi',
      email: 'me@test.com',
      password: '',
    });
  });

  it('should submit profile updates to PUT /me and reset pristine state', () => {
    fixture.detectChanges();

    component.meForm.patchValue({
      pseudo: 'after',
      email: 'after@test.com',
      password: 'StrongPass123!',
    });

    component.submit();

    expect(meServiceSpy.updateMe).toHaveBeenCalledOnceWith({
      pseudo: 'after',
      email: 'after@test.com',
      password: 'StrongPass123!',
    });
    expect(authServiceSpy.saveToken).toHaveBeenCalledOnceWith('new-token');
    expect(component.savingProfile).toBeFalse();
    expect(component.successMessage).toBe('Vos informations ont été mises à jour.');
    expect(component.errorMessage).toBe('');
    expect(component.meForm.pristine).toBeTrue();
    expect(component.meForm.getRawValue()).toEqual({
      pseudo: 'after',
      email: 'after@test.com',
      password: '',
    });
  });

  it('should not submit when the form is invalid', () => {
    fixture.detectChanges();

    component.meForm.patchValue({
      pseudo: 'ab',
      email: 'invalid-email',
      password: 'short',
    });

    component.submit();

    expect(meServiceSpy.updateMe).not.toHaveBeenCalled();
    expect(authServiceSpy.saveToken).not.toHaveBeenCalled();
    expect(component.meForm.controls.pseudo.touched).toBeTrue();
    expect(component.meForm.controls.email.touched).toBeTrue();
    expect(component.meForm.controls.password.touched).toBeTrue();
  });

  it('should show an error message when updating profile fails', () => {
    meServiceSpy.updateMe.and.returnValue(throwError(() => new Error('update failed')));
    fixture.detectChanges();

    component.meForm.patchValue({
      pseudo: 'after',
      email: 'after@test.com',
      password: 'StrongPass123!',
    });

    component.submit();

    expect(meServiceSpy.updateMe).toHaveBeenCalledTimes(1);
    expect(authServiceSpy.saveToken).not.toHaveBeenCalled();
    expect(component.savingProfile).toBeFalse();
    expect(component.errorMessage).toBe('Impossible de mettre à jour vos informations pour le moment.');
    expect(component.successMessage).toBe('');
  });

  it('should set an error when subscribed topics loading fails', () => {
    topicServiceSpy.getTopics.and.returnValue(throwError(() => new Error('topic load failed')));

    fixture.detectChanges();

    expect(topicServiceSpy.getTopics).toHaveBeenCalledTimes(1);
    expect(component.loadingSubscribedTopics).toBeFalse();
    expect(component.subscribedTopics).toEqual([]);
    expect(component.subscribedTopicsErrorMessage).toBe(
      'Impossible de charger vos thèmes abonnés pour le moment.',
    );
  });

  it('should unsubscribe a topic and remove it from the list', () => {
    fixture.detectChanges();

    const topic = component.subscribedTopics[0];

    component.unsubscribe(topic);

    expect(topicServiceSpy.unsubscribeFromTopic).toHaveBeenCalledOnceWith(10);
    expect(component.isUnsubscribing(topic)).toBeFalse();
    expect(component.subscribedTopics).toEqual([]);
    expect(component.subscribedTopicsErrorMessage).toBe('');
  });

  it('should show an error when unsubscribe fails', () => {
    topicServiceSpy.unsubscribeFromTopic.and.returnValue(
      throwError(() => new Error('unsubscribe failed')),
    );

    fixture.detectChanges();

    const topic = component.subscribedTopics[0];

    component.unsubscribe(topic);

    expect(topicServiceSpy.unsubscribeFromTopic).toHaveBeenCalledOnceWith(10);
    expect(component.subscribedTopics.length).toBe(1);
    expect(component.isUnsubscribing(topic)).toBeFalse();
    expect(component.subscribedTopicsErrorMessage).toBe(
      'Impossible de vous désabonner pour le moment.',
    );
  });

  it('should reject weak password format in profile form', () => {
    fixture.detectChanges();

    component.meForm.patchValue({
      pseudo: 'after',
      email: 'after@test.com',
      password: 'StrongPass123',
    });

    expect(component.meForm.controls.password.invalid).toBeTrue();

    component.submit();

    expect(meServiceSpy.updateMe).not.toHaveBeenCalled();
  });

  it('should accept strong password format in profile form', () => {
    fixture.detectChanges();

    component.meForm.patchValue({
      pseudo: 'after',
      email: 'after@test.com',
      password: 'StrongPass123!',
    });

    expect(component.meForm.controls.password.valid).toBeTrue();
  });
});