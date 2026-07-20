import { Component, OnInit } from '@angular/core';
import { FeedArticle, FeedService } from '../../services/feed.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent implements OnInit {
  articles: FeedArticle[] = [];
  loading = false;
  errorMessage = '';

  constructor(private readonly feedService: FeedService) {}

  ngOnInit(): void {
    this.loadFeed();
  }

  loadFeed(): void {
    this.loading = true;
    this.errorMessage = '';

    this.feedService.getFeed().subscribe({
      next: (articles) => {
        this.loading = false;
        this.articles = articles;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = "Impossible de charger le feed pour le moment.";
      },
    });
  }

  trackByArticle(index: number, article: FeedArticle): string | number {
    return article.id ?? `${article.title ?? 'article'}-${index}`;
  }
}