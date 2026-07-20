import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface FeedArticle {
  id?: number | string;
  title?: string;
  content?: string;
  description?: string;
  publishedAt?: string;
  createdAt?: string;
  updatedAt?: string;
  author?: {
    name?: string;
    pseudo?: string;
    username?: string;
  };
  authorName?: string;
}

export type FeedSort = 'ASC' | 'DESC';

interface FeedApiResponse {
  data?: FeedArticle[];
  feed?: FeedArticle[];
  articles?: FeedArticle[];
}

@Injectable({
  providedIn: 'root',
})
export class FeedService {
  private readonly feedUrl = `${environment.apiBaseUrl}feed`;

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {}

  getFeed(sort: FeedSort = 'DESC'): Observable<FeedArticle[]> {
    const token = this.authService.getToken();
    const params = new HttpParams().set('sort', sort);
    const options = token
      ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }), params }
      : { params };

    return this.http
      .get<FeedArticle[] | FeedApiResponse>(this.feedUrl, options)
      .pipe(map((response) => this.normalizeResponse(response)));
  }

  private normalizeResponse(response: FeedArticle[] | FeedApiResponse): FeedArticle[] {
    if (Array.isArray(response)) {
      return response;
    }

    if (Array.isArray(response.articles)) {
      return response.articles;
    }

    if (Array.isArray(response.feed)) {
      return response.feed;
    }

    if (Array.isArray(response.data)) {
      return response.data;
    }

    return [];
  }
}