import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError, map, tap, switchMap } from 'rxjs/operators';

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

  // Métodos para el token
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getCurrentUserId(): number | null {
    const currentUser = this.getUserDetails();
    if (currentUser && currentUser.id) {
      return currentUser.id;
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
    return localStorage.getItem(this.isLoggedInKey) === 'true';
  }

  login(credentials: {username: string, password: string}): Observable<any> {
    console.log('Enviando credenciales:', credentials);
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      switchMap((response: any) => {
        console.log('Respuesta completa del servidor:', response);

        if (response.status === 'SUCCESS') {
          // Mark as logged in
          localStorage.setItem(this.isLoggedInKey, 'true');

          // Store token if provided (though it seems your API uses cookies)
          if (response.token) {
            this.setToken(response.token);
          }

          // Get user data
          return this.validateAuthStatus().pipe(
            map(isAuthenticated => {
              if (isAuthenticated) {
                return { ...response, user: this.currentUserValue };
              }
              return response;
            })
          );
        }

        return of(response);
      }),
      catchError(error => {
        console.error('Error completo:', error);
        localStorage.removeItem(this.isLoggedInKey);
        return of({ status: 'ERROR', error: error });
      })
    );
  }

  // Método para obtener los datos del usuario actual
  fetchCurrentUser(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`).pipe(
      tap((userData: any) => {
        localStorage.setItem('currentUser', JSON.stringify(userData));
        this.currentUserSubject.next(userData);
      }),
      catchError(error => {
        console.error('Error al obtener datos del usuario:', error);
        // Clear auth state if we get a 401 error
        if (error.status === 401) {
          this.clearAuthState();
        }
        return of(null);
      })
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

  private initAuthState(): void {
    const isLoggedInFlag = localStorage.getItem(this.isLoggedInKey) === 'true';
    const hasCookieToken = document.cookie.includes('accessToken') || document.cookie.includes('refreshToken');

    if (isLoggedInFlag || hasCookieToken) {
      // Check if we're really logged in by calling the /me endpoint
      this.validateAuthStatus().subscribe(isAuthenticated => {
        if (!isAuthenticated) {
          this.clearAuthState();
        }
      });
    } else {
      this.clearAuthState();
    }
  }

  private clearAuthState(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.isLoggedInKey);
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  // Método para validar el token con el backend
  public validateAuthStatus(): Observable<boolean> {
    return this.http.get<any>(`${this.apiUrl}/me`).pipe(
      map(userData => {
        if (userData && userData.id) {
          // Store user data if validation is successful
          localStorage.setItem('currentUser', JSON.stringify(userData));
          this.currentUserSubject.next(userData);
          return true;
        }
        return false;
      }),
      catchError(() => {
        // Clear auth state on failed validation
        this.clearAuthState();
        return of(false);
      })
    );
  }

  /**
   * Check if the user has admin privileges
   */
  // Add this to AuthService
  isAdmin(): boolean {
    const currentUser = this.getUserDetails();
    // Using your logic that admin is user with id 7
    return currentUser && currentUser.id === 7;
  }
}
