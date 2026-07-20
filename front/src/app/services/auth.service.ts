import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface RegisterPayload {
  pseudo: string;
  email: string;
  password: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly registerUrl = `${environment.apiBaseUrl}auth/register`;
  private readonly loginUrl = `${environment.apiBaseUrl}auth/login`;

  constructor(private readonly http: HttpClient) {}

  register(payload: RegisterPayload): Observable<unknown> {
    return this.http.post(this.registerUrl, payload);
  }

  login(payload: LoginPayload): Observable<unknown> {
    return this.http.post(this.loginUrl, payload);
  }
}
