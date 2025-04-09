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
    // Verificar diferentes formas de autenticación
    const token = localStorage.getItem(this.tokenKey);
    const isLoggedInFlag = localStorage.getItem(this.isLoggedInKey) === 'true';
    const hasCookieToken = document.cookie.includes('accessToken') || document.cookie.includes('refreshToken');

    // Si cualquiera indica que estamos autenticados, considerar al usuario como logueado
    if (token || isLoggedInFlag || hasCookieToken) {
      this.currentUserSubject.next({ token: token || 'cookie-auth' });

      // Asegurar que la bandera isLoggedIn está establecida
      if (!isLoggedInFlag) {
        localStorage.setItem(this.isLoggedInKey, 'true');
      }
    }
  }

  // Métodos para el token
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getCurrentUserId(): number | null {
    const currentUser = this.getUserDetails();
    if (currentUser && currentUser.id) {
      return currentUser.id;
    }

    // Si no podemos obtener el ID pero estamos logueados, usar un ID por defecto para pruebas
    if (this.getIsLoggedIn()) {
      return 1; // ID por defecto para pruebas
    }

    return null;
  }

  getUserDetails(): any {
    const userData = localStorage.getItem('currentUser');
    return userData ? JSON.parse(userData) : null;
  }

  private setToken(token: string): void {
    if (token) {
      localStorage.setItem(this.tokenKey, token);
    }
  }

  // Métodos de autenticación
  getIsLoggedIn(): boolean {
    // Verificar múltiples fuentes para determinar el estado de autenticación
    const hasToken = !!this.getToken();
    const isLoggedInFlag = localStorage.getItem(this.isLoggedInKey) === 'true';
    const hasCookieToken = document.cookie.includes('accessToken') || document.cookie.includes('refreshToken');

    return hasToken || isLoggedInFlag || hasCookieToken;
  }

  login(credentials: {username: string, password: string}): Observable<any> {
    console.log('Enviando credenciales:', credentials);
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap(
        (response: any) => {
          console.log('Respuesta completa del servidor:', response);
          // Si la respuesta indica éxito, considerar al usuario como autenticado
          if (response.status === 'SUCCESS') {
            // Si hay un token en la respuesta, guardarlo
            if (response.token) {
              this.setToken(response.token);
            }

            // Marcar como logueado incluso si no hay token (puede estar usando cookies)
            localStorage.setItem(this.isLoggedInKey, 'true');
            this.currentUserSubject.next({ token: response.token || 'cookie-auth' });

            // Guardar el usuario en localStorage si viene en la respuesta
            if (response.user) {
              localStorage.setItem('currentUser', JSON.stringify(response.user));
            }
          }
        },
        error => {
          console.error('Error completo:', error);
          localStorage.removeItem(this.isLoggedInKey);
          // No hacer nada más con el error, solo registrarlo
        }
      )
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.isLoggedInKey);
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
      })
    );
  }

  // Para el componente home/index
  get currentUserValue() {
    return this.currentUserSubject.value;
  }
}
