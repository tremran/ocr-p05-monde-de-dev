import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { PostArticle, PostComment, PostService } from '../../services/post.service';

@Component({
  selector: 'app-article',
  templateUrl: './article.component.html',
  styleUrls: ['./article.component.scss'],
})
export class ArticleComponent implements OnInit {
  articleId = '';
  articles: PostArticle[] = [];
  comments: PostComment[] = [];
  loading = false;
  commentsLoading = false;
  commentSubmitting = false;
  errorMessage = '';
  commentsErrorMessage = '';
  commentSubmitErrorMessage = '';
  readonly commentForm = this.formBuilder.group({
    content: ['', [Validators.required]],
  });

  constructor(
    private readonly route: ActivatedRoute,
    private readonly postService: PostService,
    private readonly formBuilder: FormBuilder,
  ) {}

  ngOnInit(): void {
    const articleId = this.route.snapshot.paramMap.get('id_article');

    if (!articleId) {
      this.errorMessage = 'Identifiant d\'article manquant.';
      return;
    }

    this.articleId = articleId;
    this.loadArticle(articleId);
  }

  loadArticle(articleId: string): void {
    this.loading = true;
    this.errorMessage = '';
    this.commentsLoading = true;
    this.commentsErrorMessage = '';

    this.postService.getPost(articleId).subscribe({
      next: (articles) => {
        this.loading = false;
        this.articles = articles;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Impossible de charger l\'article pour le moment.';
      },
    });

    this.postService.getComments(articleId).subscribe({
      next: (comments) => {
        this.commentsLoading = false;
        this.comments = comments;
      },
      error: () => {
        this.commentsLoading = false;
        this.commentsErrorMessage = 'Impossible de charger les commentaires pour le moment.';
      },
    });
  }

  trackByArticle(index: number, article: PostArticle): string | number {
    return article.id ?? `${article.title ?? 'article'}-${index}`;
  }

  trackByComment(index: number, comment: PostComment): string | number {
    return comment.id ?? `${comment.content ?? 'comment'}-${index}`;
  }

  submitComment(): void {
    if (!this.articleId || this.commentSubmitting) {
      return;
    }

    const rawContent = this.commentForm.controls.content.value ?? '';
    const content = rawContent.trim();

    if (!content) {
      this.commentForm.controls.content.setErrors({ required: true });
      this.commentForm.controls.content.markAsTouched();
      return;
    }

    this.commentSubmitting = true;
    this.commentSubmitErrorMessage = '';

    this.postService.addComment(this.articleId, content).subscribe({
      next: (comment) => {
        this.commentSubmitting = false;
        this.comments = [...this.comments, comment];
        this.commentForm.reset();
      },
      error: () => {
        this.commentSubmitting = false;
        this.commentSubmitErrorMessage = 'Impossible d\'ajouter le commentaire pour le moment.';
      },
    });
  }

  reload(): void {
    if (!this.articleId) {
      return;
    }

    this.loadArticle(this.articleId);
  }
}