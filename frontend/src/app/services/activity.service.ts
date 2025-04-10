import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface ActivityDto {
  id: number;
  name: string;
  description: string;
  category: string;
  price: number;
  imageUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PlaceDto {
  id: number;
  name: string;
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
export class ActivityService {
  // Usando ruta relativa que será redirigida por el proxy a la URL correcta
  private readonly API_URL = '/api/activities';


  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

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

  getActivities(page: number = 0, size: number = 8): Observable<PageResponse<ActivityDto>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<ActivityDto>>(
      `${this.API_URL}/pageable`,
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

  getActivityById(id: number): Observable<ActivityDto> {
    return this.http.get<ActivityDto>(
      `${this.API_URL}/${id}`,
      {
        headers: this.getHeaders()
      }
    ).pipe(
      catchError(this.handleError)
    );
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

  getRecommendedActivities(userId: number, page: number = 0, size: number = 4): Observable<any> {
    // Asegúrate de que la URL sea correcta según tu API
    const url = `${this.API_URL}/users/${userId}/recommended-activities?page=${page}&size=${size}`;
    console.log('Solicitando recomendaciones a:', url);
    return this.http.get<any>(url, { observe: 'response' });
  }

  // Método alternativo por si la ruta es diferente
  getRecommendedActivitiesAlternative(userId: number, page: number = 0, size: number = 4): Observable<any> {
    // Prueba con esta URL alternativa si la anterior no funciona
    const url = `https://localhost:8443/api/users/${userId}/recommended-activities?page=${page}&size=${size}`;
    console.log('Solicitando recomendaciones (ruta alternativa):', url);
    return this.http.get<any>(url, { observe: 'response' });
  }

  getActivityImageUrl(activityId: number): string {
    return `${this.API_URL}/${activityId}/image`;
  }

  // Método para obtener lugares
  getPlaces(): Observable<PlaceDto[]> {
    return this.http.get<PlaceDto[]>(`${this.API_URL}/places`);
  }

  // Método para buscar actividades por lugar
  searchActivitiesByPlace(placeId: number, page: number = 0, size: number = 8): Observable<any> {
    return this.http.get<PageResponse<ActivityDto>>(
      `${this.API_URL}/search?placeId=${placeId}&page=${page}&size=${size}`,
      { observe: 'response' }
    );
  }

  getUserSubscribedActivities(userId:number,page:number = 0, size: number = 4):Observable<PageResponse<ActivityDto>>{
    const params = new HttpParams()
    .set('page',page.toString())
    .set('size',size.toString());
    return this.http.get<PageResponse<ActivityDto>>(`${this.API_URL}/users/${userId}/subscribed-activities`,{params});
  }
}
