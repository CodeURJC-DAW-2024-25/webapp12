import { Injectable } from '@angular/core';
import { HttpClient , HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject ,throwError} from 'rxjs';
import { tap,catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { UserDto } from '../dtos/user.dto';
import { AuthService } from './auth.service';

export interface UserUpdateDto {
  name: string;
  surname: string;
  dni: string;
  phone: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  private currentUserSubject = new BehaviorSubject<any>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  private apiUrl = 'api/auth'; // Replace with your actual API URL
  private api_Url = '/api/users'; 

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  

  /**
   * Login user with email and password
   */
  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap(response => {
          this.storeToken(response.token);
          this.storeUserDetails(response.user);
          this.isLoggedInSubject.next(true);
          this.currentUserSubject.next(response.user);
        })
      );
  }

  /**
   * Register a new user
   */
  register(user: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, user);
  }

  /**
   * Logout the current user
   */
  logout(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/logout`, {})
      .pipe(
        tap(() => {
          this.clearStorage();
          this.isLoggedInSubject.next(false);
          this.currentUserSubject.next(null);
          this.router.navigate(['/']);
        })
      );
  }

  /**
   * Get the current user's profile
   */
  getUserProfile(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/me`)
      .pipe(
        tap(user => {
          this.storeUserDetails(user);
          this.currentUserSubject.next(user);
        })
      );
  }

  /**
   * Update the user's profile
   */
  updateProfile(userData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/profile`, userData)
      .pipe(
        tap(updatedUser => {
          this.storeUserDetails(updatedUser);
          this.currentUserSubject.next(updatedUser);
        })
      );
  }

  /**
   * Change user password
   */
  changePassword(passwordData: { currentPassword: string, newPassword: string }): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/change-password`, passwordData);
  }

  /**
   * Request password reset
   */
  forgotPassword(email: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/forgot-password`, { email });
  }

  /**
   * Reset password with token
   */
  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/reset-password`, { token, newPassword });
  }

  /**
   * Check if the user is logged in
   */
  isAuthenticated(): boolean {
    return this.hasToken();
  }

  /**
   * Get the current user's details
   */
  getUserDetails(): any {
    return this.getUserFromStorage();
  }

  /**
   * Check if the current user has a specific role
   */
  hasRole(role: string): boolean {
    const user = this.getUserFromStorage();
    return user?.roles?.includes(role) || false;
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

  getUserById(userId: number): Observable<UserDto> {
    return this.http.get<UserDto>(`/api/users/${userId}`);
  }

  // Private helper methods
  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  private storeToken(token: string): void {
    localStorage.setItem('token', token);
  }

  private storeUserDetails(user: any): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  private getUserFromStorage(): any {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  private clearStorage(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
      console.error('Error in ActivityService:', error);
  
      let errorMessage = 'An unknown error occurred';
  
      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Error: ${error.error.message}`;
      } else if (error.status === 0) {
        // Network error
        errorMessage = 'Network error: Please check your internet connection';
      } else {
        // Server-side error
        errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      }
  
      return throwError(() => new Error(errorMessage));
    }

  private getHeaders(): HttpHeaders {
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      });
  
      const token = this.authService.getToken();
      if (token) {
        return headers.set('Authorization', `Bearer ${token}`);
      }
      return headers;
    }

  updateUser(id:number,userData:UserUpdateDto):Observable<UserDto>{
    return this.http.put<UserDto>(`${this.api_Url}/${id}`,userData);
  }

  updateUserImage(id:number,formData:FormData):Observable<any>{
    return this.http.put(`${this.api_Url}/${id}/image`,formData);
  }

  deleteUserImage(id:number):Observable<any>{
    return this.http.delete(`${this.api_Url}/${id}/image`);
  }

  deleteUser(id:number):Observable<any>{
    return this.http.delete(`${this.api_Url}/${id}`, { responseType: 'text' as 'json' });
  }

  getUserImageUrl(userId: number): string {
    const imageUrl = `${this.api_Url}/${userId}/image`;
    console.log('URL de imagen generada:', imageUrl);
    return imageUrl;
  }
  

  getUsers(page: number = 0, size: number = 8): Observable<PageResponse<UserDto>> {
      const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());
  
      return this.http.get<PageResponse<UserDto>>(
        `${this.api_Url}/pageable`,
        {
          params,
          headers: this.getHeaders()
        }
      ).pipe(
        tap(response => {
          console.log('Response from API:', response);
        }),
        catchError(this.handleError)
      );
    }
}
