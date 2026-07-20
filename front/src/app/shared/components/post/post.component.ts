import { Component, Input } from '@angular/core';
import { FeedArticle } from '../../../services/feed.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss'],
})
export class PostComponent {
  @Input() article: FeedArticle | null = null;

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
}