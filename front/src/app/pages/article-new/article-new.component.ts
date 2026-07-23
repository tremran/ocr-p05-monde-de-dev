import { Component, DestroyRef, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable, catchError, of, tap } from 'rxjs';
import { PostService } from '../../services/post.service';
import { Topic, TopicService } from '../../services/topic.service';

@Component({
    selector: 'app-article-new',
    templateUrl: './article-new.component.html',
    styleUrls: ['./article-new.component.scss'],
    standalone: false
})
export class ArticleNewComponent implements OnInit {
  readonly articleForm = this.fb.nonNullable.group({
    topicId: ['', [Validators.required]],
    title: ['', [Validators.required, Validators.minLength(3)]],
    content: ['', [Validators.required]],
  });

  topics$!: Observable<Topic[]>;
  loadingTopics = true;
  savingArticle = false;
  errorMessage = '';
  topicErrorMessage = '';
  successMessage = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly postService: PostService,
    private readonly topicService: TopicService,
    private readonly router: Router,
    private readonly destroyRef: DestroyRef,
  ) {}

  ngOnInit(): void {
    this.topics$ = this.topicService.getTopics()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        tap((topics: Topic[]) => {
          this.loadingTopics = false;
          this.topicErrorMessage = '';
        }),
        catchError((err: any) => {
          this.loadingTopics = false;
          this.topicErrorMessage = 'Impossible de charger les thèmes pour le moment.';
          return of([] as Topic[]);
        })
      );
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
      .pipe(takeUntilDestroyed(this.destroyRef))
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
