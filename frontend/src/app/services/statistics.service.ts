import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interfaces para los datos de la API
export interface ActivitiesByMonthDto {
  month: number;
  count: number;
}

export interface ReviewStatisticsDto {
  valoration: number;
  count: number;
}

interface ActivityData {
  month: number;
  count: number;
}

export interface GeneralStatisticsDto {
  userCount: number;
  activityCount: number;
  placeCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private apiUrl = '/api/statistics';

  constructor(private http: HttpClient) { }

getActivitiesByMonth(): Observable<ActivityData[]> {
  return this.http.get<ActivityData[]>(`${this.apiUrl}/activities-by-month`);
}

  getReviewStatistics(): Observable<ReviewStatisticsDto[]> {
    return this.http.get<ReviewStatisticsDto[]>(`${this.apiUrl}/review-statistics`);
  }

  getGeneralStatistics(): Observable<GeneralStatisticsDto> {
    return this.http.get<GeneralStatisticsDto>(`${this.apiUrl}/general-statistics`);
  }
}
