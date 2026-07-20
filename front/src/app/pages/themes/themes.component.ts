import { Component, OnInit } from '@angular/core';
import { Topic, TopicService } from '../../services/topic.service';

@Component({
  selector: 'app-themes',
  templateUrl: './themes.component.html',
  styleUrls: ['./themes.component.scss'],
})
export class ThemesComponent implements OnInit {
  topics: Topic[] = [];
  loading = false;
  errorMessage = '';
  subscribeErrorMessage = '';
  private readonly subscribingTopicIds = new Set<string | number>();

  constructor(private readonly topicService: TopicService) {}

  ngOnInit(): void {
    this.loadTopics();
  }

  loadTopics(): void {
    this.loading = true;
    this.errorMessage = '';

    this.topicService.getTopics().subscribe({
      next: (topics) => {
        this.loading = false;
        this.topics = topics;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Impossible de charger les thèmes pour le moment.';
      },
    });
  }

  trackByTopic(index: number, topic: Topic): string | number {
    return topic.id ?? `${topic.name ?? 'topic'}-${index}`;
  }

  isSubscribing(topic: Topic): boolean {
    if (topic.id === undefined || topic.id === null) {
      return false;
    }

    return this.subscribingTopicIds.has(topic.id);
  }

  subscribe(topic: Topic): void {
    if (topic.registered) {
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

    this.topicService.subscribeToTopic(topic.id).subscribe({
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