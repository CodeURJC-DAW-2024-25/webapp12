import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError, map, tap, switchMap } from 'rxjs/operators';
import { UserDto } from '../dtos/user.dto';

export interface RegisterUser {
  email: string;
  name: string;
  surname: string;
  dni: string;
  phone: string;
  password: string;
  roles?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';
  private isLoggedInKey = 'isLoggedIn';
  private currentUserSubject = new BehaviorSubject<UserDto | null>(null);
  public currentUser$: Observable<UserDto | null> = this.currentUserSubject.asObservable();
  private tokenKey = 'auth_token';

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
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
    return null;
  }

  getUserDetails(): UserDto | null {
    return this.currentUserSubject.getValue();
  }

  updateUserDetails(user: UserDto): void {
    this.currentUserSubject.next(user);
    
    localStorage.setItem('currentUser', JSON.stringify(user));
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
    // Asegúrate de que se devuelva siempre un booleano
    return currentUser !== null && currentUser.id === 7;
  }




  // MÉTODO DE REGISTRO
  register(user: RegisterUser): Observable<any> {
    // Preparar el objeto de usuario para registro
    const registrationData = {...user};

    // Si no se especifican roles, usar USER por defecto
    if (!registrationData.roles || registrationData.roles.length === 0) {
      registrationData.roles = ['USER'];
    }

    // Asegurar que no haya prefijos ROLE_ en los roles
    registrationData.roles = registrationData.roles.map(role =>
      role.startsWith('ROLE_') ? role.substring(5) : role
    );

    return this.http.post('/api/users/', registrationData).pipe(
      tap(response => console.log('Usuario registrado con éxito:', response)),
      catchError(error => {
        console.error('Error al registrar usuario:', error);
        return of({ status: 'ERROR', error });
      })
    );
  }
}
