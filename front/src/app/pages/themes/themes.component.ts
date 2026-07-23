import { Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable, catchError, of, tap } from 'rxjs';
import { Topic, TopicService } from '../../services/topic.service';

@Component({
    selector: 'app-themes',
    templateUrl: './themes.component.html',
    styleUrls: ['./themes.component.scss'],
    standalone: false
})
export class ThemesComponent implements OnInit {
  topics$!: Observable<Topic[]>;
  loading = true;
  errorMessage = '';
  subscribeErrorMessage = '';
  private readonly subscribingTopicIds = new Set<string | number>();

  constructor(
    private readonly topicService: TopicService,
    private readonly destroyRef: DestroyRef,
  ) {}

  ngOnInit(): void {
    this.loadTopics();
  }

  loadTopics(): void {
    this.loading = true;
    this.topics$ = this.topicService.getTopics()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        tap(() => {
          this.loading = false;
          this.errorMessage = '';
        }),
        catchError((err: any) => {
          this.loading = false;
          this.errorMessage = 'Impossible de charger les thèmes pour le moment.';
          return of([] as Topic[]);
        })
      );
  }

  isSubscribing(topic: Topic): boolean {
    if (topic.id === undefined || topic.id === null) {
      return false;
    }

    return this.subscribingTopicIds.has(topic.id);
  }

  subscribe(topic: Topic | null): void {
    if (!topic || topic.registered) {
      return;
    }

    if (topic.id === undefined || topic.id === null) {
      this.subscribeErrorMessage = 'Impossible de s\'abonner à ce thème.';
      return;
    }

    if (this.subscribingTopicIds.has(topic.id)) {
      return;
    }

    this.subscribeErrorMessage = '';
    this.subscribingTopicIds.add(topic.id);

    this.topicService.subscribeToTopic(topic.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.subscribingTopicIds.delete(topic.id as string | number);
          topic.registered = true;
        },
        error: () => {
          this.subscribingTopicIds.delete(topic.id as string | number);
          this.subscribeErrorMessage = 'L\'abonnement a échoué. Veuillez réessayer.';
        },
      });
  }
}