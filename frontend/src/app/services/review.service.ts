import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { PageResponse } from './activity.service';

export interface ReviewDto {
  id: number;
  comment: string;            // Cambio de description a comment
  starsValue: number;
  creationDate: string;       // Fecha de creación
  userFullName: string;       // Nombre completo del usuario (no en objeto anidado)
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private readonly API_URL = '/api/reviews';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getReviewsByActivity(activityId: number, page: number = 0, size: number = 5): Observable<PageResponse<ReviewDto>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<ReviewDto>>(
      `${this.API_URL}/activity/${activityId}`,
      { params }
    ).pipe(
      tap(response => {
        console.log('Reviews response:', response);
        // Log the structure of the first review if available
        if (response?.content?.length > 0) {
          console.log('First review structure:', response.content[0]);
        }
      }),
      catchError(error => {
        console.error('Error fetching reviews:', error);
        return throwError(() => new Error('Error fetching reviews'));
      })
    );
  }

  submitReview(activityId: number, review: { starsValue: number, description: string }): Observable<ReviewDto> {
    // Crear un nuevo objeto con la estructura correcta que espera el backend
    const requestBody = {
      starsValue: review.starsValue,
      comment: review.description // Mapear 'description' a 'comment' para el backend
    };

    const token = this.authService.getToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return this.http.post<ReviewDto>(
      `${this.API_URL}/activity/${activityId}`,
      requestBody,
      { headers }
    ).pipe(
      catchError(error => {
        console.error('Error submitting review:', error);
        return throwError(() => new Error('Error submitting review'));
      })
    );
  }
}
