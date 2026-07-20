import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface MeResponse {
  id?: number | string;
  email?: string;
  pseudo?: string;
  token?: string;
}

export interface UpdateMePayload {
  email: string;
  pseudo: string;
  password: string;
}

@Injectable({
  providedIn: 'root',
})
export class MeService {
  private readonly meUrl = `${environment.apiBaseUrl}me`;

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {}

  getMe(): Observable<MeResponse> {
    const token = this.authService.getToken();
    const options = token
      ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) }
      : {};

    return this.http.get<MeResponse>(this.meUrl, options);
  }

  updateMe(payload: UpdateMePayload): Observable<MeResponse> {
    const token = this.authService.getToken();
    const options = token
      ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) }
      : {};

    return this.http.put<MeResponse>(this.meUrl, payload, options);
  }
}
