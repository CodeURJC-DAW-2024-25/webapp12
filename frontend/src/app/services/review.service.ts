import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface ReviewDto {
  id: number;
  starsValue: number;
  description: string;
  createdAt: string;
  user: {
    id: number;
    name: string;
  };
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
export class ReviewService {
  private readonly API_URL = '/api/reviews';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    console.log('Token enviado:', token);
    return token ? headers.set('Authorization', `Bearer ${token}`) : headers;
    

  }

  getReviewsByActivity(activityId: number, page: number = 0, size: number = 5): Observable<PageResponse<ReviewDto>> {
    console.log(`Llamando a /api/activities/${activityId}/reviews?page=${page}&size=${size}`);
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<ReviewDto>>(
      `/api/activities/${activityId}/reviews`,
      {
        params,
        headers: this.getHeaders()
      }
    ).pipe(
      catchError((err) => {
        console.error('Error en getReviewsByActivity:', err);
        return throwError(() => new Error('Error al cargar reseñas'));
      })
    );
  }
}
