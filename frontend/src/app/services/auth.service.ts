import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';
  private isLoggedInKey = 'isLoggedIn';
  private currentUserSubject = new BehaviorSubject<any>(null);
  private tokenKey = 'auth_token';

  constructor(private http: HttpClient) {
    this.initAuthState();
  }

  private initAuthState(): void {
    const token = localStorage.getItem(this.tokenKey);
    if (token) {
      this.currentUserSubject.next({ token });
    }
  }

  // Métodos para el token
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  // Métodos de autenticación
  getIsLoggedIn(): boolean {
    return !!this.getToken();
  }

  login(credentials: {username: string, password: string}): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        if (response.status === 'SUCCESS' && response.token) {
          this.setToken(response.token);
          this.currentUserSubject.next({ token: response.token });
        }
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        localStorage.removeItem(this.tokenKey);
        this.currentUserSubject.next(null);
      })
    );
  }

  // Para el componente home/index
  get currentUserValue() {
    return this.currentUserSubject.value;
  }
}
