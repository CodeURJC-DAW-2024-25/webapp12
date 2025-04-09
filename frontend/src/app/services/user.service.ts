import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  private currentUserSubject = new BehaviorSubject<any>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  private apiUrl = 'api/auth'; // Replace with your actual API URL

  constructor(
    private http: HttpClient,
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
}
