import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { Topic, TopicService } from '../../services/topic.service';

@Component({
  selector: 'app-article-new',
  templateUrl: './article-new.component.html',
  styleUrls: ['./article-new.component.scss'],
})
export class ArticleNewComponent implements OnInit {
  readonly articleForm = this.fb.nonNullable.group({
    topicId: ['', [Validators.required]],
    title: ['', [Validators.required, Validators.minLength(3)]],
    content: ['', [Validators.required]],
  });

  topics: Topic[] = [];
  loadingTopics = false;
  savingArticle = false;
  errorMessage = '';
  topicErrorMessage = '';
  successMessage = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly postService: PostService,
    private readonly topicService: TopicService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.loadTopics();
  }

  loadTopics(): void {
    this.loadingTopics = true;
    this.topicErrorMessage = '';

    this.topicService.getTopics().subscribe({
      next: (topics) => {
        this.loadingTopics = false;
        this.topics = topics;
      },
      error: () => {
        this.loadingTopics = false;
        this.topicErrorMessage = 'Impossible de charger les thèmes pour le moment.';
      },
    });
  }

  trackByTopic(index: number, topic: Topic): string | number {
    return topic.id ?? `${topic.name ?? 'topic'}-${index}`;
  }

  submit(): void {
    if (this.articleForm.invalid || this.savingArticle) {
      this.articleForm.markAllAsTouched();
      return;
    }

    this.savingArticle = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.articleForm.getRawValue();
    const parsedTopicId = Number(formValue.topicId);
    const topicId = Number.isNaN(parsedTopicId) ? formValue.topicId : parsedTopicId;

    this.postService
      .createPost({
        topicId,
        title: formValue.title,
        content: formValue.content,
        publishedAt: this.getTodayDateString(),
      })
      .subscribe({
        next: () => {
          this.savingArticle = false;
          this.successMessage = 'Article publié avec succès.';
          this.articleForm.reset();
          this.router.navigate(['/feed']);
        },
        error: () => {
          this.savingArticle = false;
          this.errorMessage = 'Impossible de publier l\'article pour le moment.';
        },
      });
  }

  private getTodayDateString(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
}
