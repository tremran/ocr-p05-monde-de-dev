import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface Topic {
  id?: number | string;
  name?: string;
  description?: string;
  registered?: boolean;
}

interface TopicApiResponse {
  data?: Topic[];
  topics?: Topic[];
}

@Injectable({
  providedIn: 'root',
})
export class TopicService {
  private readonly topicUrl = `${environment.apiBaseUrl}topic`;
  private readonly subscriptionUrl = `${environment.apiBaseUrl}subscription`;

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {}

  getTopics(): Observable<Topic[]> {
    const options = this.buildAuthOptions();

    return this.http
      .get<Topic[] | TopicApiResponse>(this.topicUrl, options)
      .pipe(map((response) => this.normalizeResponse(response)));
  }

  subscribeToTopic(topicId: number | string): Observable<unknown> {
    const options = this.buildAuthOptions();
    return this.http.post(`${this.subscriptionUrl}/${topicId}`, {}, options);
  }

  private buildAuthOptions(): { headers?: HttpHeaders } {
    const token = this.authService.getToken();

    if (!token) {
      return {};
    }

    return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
  }

  private normalizeResponse(response: Topic[] | TopicApiResponse): Topic[] {
    if (Array.isArray(response)) {
      return response;
    }

    if (Array.isArray(response.topics)) {
      return response.topics;
    }

    if (Array.isArray(response.data)) {
      return response.data;
    }

    return [];
  }
}