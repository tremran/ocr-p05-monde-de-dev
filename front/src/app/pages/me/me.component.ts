import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { MeService } from '../../services/me.service';
import { Topic, TopicService } from '../../services/topic.service';

const OPTIONAL_PASSWORD_RULES = /^$|(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[=+_\-$#!?]).{9,}$/;

@Component({
  selector: 'app-me',
  templateUrl: './me.component.html',
  styleUrls: ['./me.component.scss'],
})
export class MeComponent implements OnInit {
  readonly meForm = this.fb.nonNullable.group({
    pseudo: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.pattern(OPTIONAL_PASSWORD_RULES)]],
  });

  loadingProfile = false;
  loadingSubscribedTopics = false;
  savingProfile = false;
  unsubscribingTopicIds = new Set<string | number>();
  errorMessage = '';
  subscribedTopicsErrorMessage = '';
  successMessage = '';
  subscribedTopics: Topic[] = [];

  constructor(
    private readonly fb: FormBuilder,
    private readonly meService: MeService,
    private readonly authService: AuthService,
    private readonly topicService: TopicService,
  ) {}

  ngOnInit(): void {
    this.loadMe();
    this.loadSubscribedTopics();
  }

  loadMe(): void {
    this.loadingProfile = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.meService.getMe().subscribe({
      next: (me) => {
        this.loadingProfile = false;
        this.meForm.patchValue({
          pseudo: me.pseudo ?? '',
          email: me.email ?? '',
          password: '',
        });
      },
      error: () => {
        this.loadingProfile = false;
        this.errorMessage = 'Impossible de charger vos informations pour le moment.';
      },
    });
  }

  loadSubscribedTopics(): void {
    this.loadingSubscribedTopics = true;
    this.subscribedTopicsErrorMessage = '';

    this.topicService.getTopics().subscribe({
      next: (topics) => {
        this.loadingSubscribedTopics = false;
        this.subscribedTopics = topics.filter((topic) => topic.registered === true);
      },
      error: () => {
        this.loadingSubscribedTopics = false;
        this.subscribedTopicsErrorMessage = 'Impossible de charger vos thèmes abonnés pour le moment.';
      },
    });
  }

  trackByTopic(index: number, topic: Topic): string | number {
    return topic.id ?? `${topic.name ?? 'topic'}-${index}`;
  }

  isUnsubscribing(topic: Topic): boolean {
    if (topic.id === undefined || topic.id === null) {
      return false;
    }

    return this.unsubscribingTopicIds.has(topic.id);
  }

  unsubscribe(topic: Topic): void {
    if (topic.id === undefined || topic.id === null) {
      return;
    }

    if (this.unsubscribingTopicIds.has(topic.id)) {
      return;
    }

    this.subscribedTopicsErrorMessage = '';
    this.unsubscribingTopicIds.add(topic.id);

    this.topicService.unsubscribeFromTopic(topic.id).subscribe({
      next: () => {
        this.unsubscribingTopicIds.delete(topic.id as string | number);
        this.subscribedTopics = this.subscribedTopics.filter((existingTopic) => existingTopic.id !== topic.id);
      },
      error: () => {
        this.unsubscribingTopicIds.delete(topic.id as string | number);
        this.subscribedTopicsErrorMessage = 'Impossible de vous désabonner pour le moment.';
      },
    });
  }

  submit(): void {
    if (this.meForm.invalid || this.loadingProfile || this.savingProfile) {
      this.meForm.markAllAsTouched();
      return;
    }

    this.savingProfile = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.meForm.getRawValue();
    this.meService
      .updateMe({
        pseudo: formValue.pseudo,
        email: formValue.email,
        password: formValue.password,
      })
      .subscribe({
        next: (me) => {
          this.savingProfile = false;
          if (me.token) {
            this.authService.saveToken(me.token);
          }
          this.successMessage = 'Vos informations ont été mises à jour.';
          this.meForm.patchValue({
            pseudo: me.pseudo ?? formValue.pseudo,
            email: me.email ?? formValue.email,
            password: '',
          });
          this.meForm.markAsPristine();
          this.meForm.markAsUntouched();
        },
        error: () => {
          this.savingProfile = false;
          this.errorMessage = 'Impossible de mettre à jour vos informations pour le moment.';
        },
      });
  }
}
