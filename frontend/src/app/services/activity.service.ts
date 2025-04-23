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
  imageUrl?: string;
  image?:boolean;
  activityDate: string;
  vacancy?: number;
  place?: PlaceDto;
  createdAt?: string;
  updatedAt?: string;
}

export interface PlaceDto {
  id: number;
  name: string;
  description: string;
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

  getUserSubscribedActivitiesCount(userId:number):Observable<number>{
    return this.http.get<number>(`${this.API_URL}/users/${userId}/subscribed-activities/count`);
  }

  deleteActivity(id:number):Observable<any>{
    return this.http.delete(`${this.API_URL}/${id}`, { responseType: 'text' as 'json' });
  }

  isUserSubscribed(activityId: number): Observable<boolean> {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      return new Observable(subscriber => {
        subscriber.next(false);
        subscriber.complete();
      });
    }

    return this.http.get<boolean>(`${this.API_URL}/${activityId}/user/${userId}`);
  }

  reserveActivity(activityId: number): Observable<Blob> {
    const token = this.authService.getToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return this.http.post(`${this.API_URL}/${activityId}/reserve`, {}, {
      headers: headers,
      responseType: 'blob'
    });
  }

  downloadReservationPdf(activityId: number): Observable<Blob> {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      throw new Error('Usuario no autenticado');
    }

    const token = this.authService.getToken();
    let headers = new HttpHeaders();

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return this.http.get(`${this.API_URL}/${activityId}/user/${userId}/reservation-pdf`, {
      headers: headers,
      responseType: 'blob'
    });
  }

  createActivity(activityData: any): Observable<ActivityDto> {
    const token = this.authService.getToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    // Crear un objeto NewActivityDto según lo espera el backend
    const newActivityDto = {
      name: activityData.name,
      category: activityData.category,
      description: activityData.description,
      imageBoolean: activityData.image || false, // Si hay imagen, será true
      vacancy: activityData.vacancy,
      creationDate: new Date(), // Fecha actual
      activityDate: new Date(activityData.activityDate), // Convertir string a Date
      placeId: Number(activityData.placeId) // Asegurarse de que sea un número
    };

    return this.http.post<ActivityDto>(
      `${this.API_URL}/`,
      newActivityDto,
      {
        headers: headers
      }
    ).pipe(
      catchError(this.handleError)
    );
  }

  // Método separado para subir la imagen si es necesario
  uploadActivityImage(activityId: number, imageFile: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', imageFile);

    const token = this.authService.getToken();
    let headers = new HttpHeaders();

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return this.http.post(
      `${this.API_URL}/${activityId}/image`,
      formData,
      {
        headers: headers,
        responseType: 'text'
      }
    ).pipe(
      catchError(this.handleError)
    );
  }


}
