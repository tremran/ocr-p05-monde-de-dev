import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface RegisterPayload {
  pseudo: string;
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly registerUrl = 'http://localhost:3001/api/v1/auth/register';

  constructor(private readonly http: HttpClient) {}

  register(payload: RegisterPayload): Observable<unknown> {
    return this.http.post(this.registerUrl, payload);
  }
}
