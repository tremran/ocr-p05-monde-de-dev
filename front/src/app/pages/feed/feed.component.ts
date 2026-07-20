import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FeedArticle, FeedService, FeedSort } from '../../services/feed.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent implements OnInit {
  articles: FeedArticle[] = [];
  selectedSort: FeedSort = 'DESC';
  loading = false;
  errorMessage = '';

  constructor(
    private readonly feedService: FeedService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.loadFeed();
  }

  loadFeed(): void {
    this.loading = true;
    this.errorMessage = '';

    this.feedService.getFeed(this.selectedSort).subscribe({
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

  goToNewArticle(): void {
    this.router.navigate(['/article/nouveau']);
  }

  toggleSortByPublishedAt(): void {
    this.selectedSort = this.selectedSort === 'DESC' ? 'ASC' : 'DESC';
    this.loadFeed();
  }

  get sortArrow(): string {
    return this.selectedSort === 'ASC' ? '↑' : '↓';
  }
}