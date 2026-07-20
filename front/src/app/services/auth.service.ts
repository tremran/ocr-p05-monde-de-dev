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

export interface LoginResponse {
  token?: string;
  data?: {
    token?: string;
  };
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly tokenStorageKey = 'auth_token';
  private readonly registerUrl = `${environment.apiBaseUrl}auth/register`;
  private readonly loginUrl = `${environment.apiBaseUrl}auth/login`;

  constructor(private readonly http: HttpClient) {}

  register(payload: RegisterPayload): Observable<unknown> {
    return this.http.post(this.registerUrl, payload);
  }

  login(payload: LoginPayload): Observable<LoginResponse> {
    return this.http.post(this.loginUrl, payload);
  }

  saveToken(token: string): void {
    localStorage.setItem(this.tokenStorageKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenStorageKey);
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenStorageKey);
  }

  saveTokenFromLoginResponse(response: LoginResponse): boolean {
    const token = response.token ?? response.data?.token;

    if (!token) {
      return false;
    }

    this.saveToken(token);
    return true;
  }
}
