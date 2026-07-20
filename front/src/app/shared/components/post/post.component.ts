import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { FeedArticle } from '../../../services/feed.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss'],
})
export class PostComponent {
  @Input() article: FeedArticle | null = null;

  constructor(private readonly router: Router) {}

  get author(): string {
    if (!this.article) {
      return 'Auteur inconnu';
    }

    return (
      this.article.author?.pseudo ||
      'Auteur inconnu'
    );
  }

  get summary(): string {
    if (!this.article) {
      return '';
    }

    return this.article.content?.substring(0, 50) + '...' || '';
  }

  get content(): string {
    if (!this.article) {
      return '';
    }

    return this.article.content || '';
  }

  openArticle(): void {
    if (this.article?.id === undefined || this.article?.id === null) {
      return;
    }

    this.router.navigate(['/article', this.article.id]);
  }
}