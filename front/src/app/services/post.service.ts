import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface PostArticle {
  id?: number | string;
  title?: string;
  content?: string;
  createdAt?: string;
  updatedAt?: string;
  publishedAt?: string;
  topic?: {
    id?: number | string;
    name?: string;
    description?: string;
  };
  author?: {
    email?: string;
    pseudo?: string;
  };
}

export interface PostComment {
  id?: number | string;
  content?: string;
  createdAt?: string;
  updatedAt?: string;
  author?: {
    email?: string;
    pseudo?: string;
  };
}

export interface CreatePostPayload {
  topicId: number | string;
  title: string;
  content: string;
  publishedAt: string;
}

interface PostApiResponse {
  data?: PostArticle;
  article?: PostArticle;
  post?: PostArticle;
  articles?: PostArticle[];
}

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private readonly postUrl = `${environment.apiBaseUrl}post`;

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {}

  getPost(postId: number | string): Observable<PostArticle[]> {
    const options = this.buildAuthOptions();

    return this.http
      .get<PostArticle | PostArticle[] | PostApiResponse>(`${this.postUrl}/${postId}`, options)
      .pipe(map((response) => this.normalizeResponse(response)));
  }

  getComments(postId: number | string): Observable<PostComment[]> {
    const options = this.buildAuthOptions();

    return this.http.get<PostComment[]>(`${this.postUrl}/${postId}/comment`, options);
  }

  addComment(postId: number | string, content: string): Observable<PostComment> {
    const options = this.buildAuthOptions();

    return this.http.post<PostComment>(`${this.postUrl}/${postId}/comment`, { content }, options);
  }

  createPost(payload: CreatePostPayload): Observable<PostArticle> {
    const options = this.buildAuthOptions();

    return this.http.post<PostArticle>(this.postUrl, payload, options);
  }

  private buildAuthOptions(): { headers?: HttpHeaders } {
    const token = this.authService.getToken();

    if (!token) {
      return {};
    }

    return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
  }

  private normalizeResponse(response: PostArticle | PostArticle[] | PostApiResponse): PostArticle[] {
    if (Array.isArray(response)) {
      return response;
    }

    const apiResponse = response as PostApiResponse;

    if (Array.isArray(apiResponse.articles)) {
      return apiResponse.articles;
    }

    if (apiResponse.article) {
      return [apiResponse.article];
    }

    if (apiResponse.post) {
      return [apiResponse.post];
    }

    if (apiResponse.data) {
      return [apiResponse.data];
    }

    return [apiResponse as PostArticle];
  }
}