import { Component, OnInit } from '@angular/core';
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
  errorMessage = '';
  commentsErrorMessage = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly postService: PostService,
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

  reload(): void {
    if (!this.articleId) {
      return;
    }

    this.loadArticle(this.articleId);
  }
}