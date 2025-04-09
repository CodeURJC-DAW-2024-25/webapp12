import { Component, OnInit } from '@angular/core';
import { ActivityService, ActivityDto, PageResponse } from '../services/activity.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {
  recommendedActivities: ActivityDto[] = [];
  allActivities: ActivityDto[] = [];
  currentRecommendedPage = 0;
  currentActivitiesPage = 0;
  recommendedTotalPages = 0;
  activitiesTotalPages = 0;
  hasMoreRecommended = true;
  hasMoreActivities = true;
  isLoading = false;
  isLoggedIn = false;
  errorMessage: string | null = null;

  constructor(
    private activityService: ActivityService,
    public authService: AuthService,
    private router: Router
  ) {
    this.isLoggedIn = this.authService.getIsLoggedIn();
  }

  ngOnInit(): void {
    this.loadInitialData();
    this.removeProblematicScript();
  }

  private removeProblematicScript(): void {
    // Eliminar el script problemático si es necesario
    const problematicScripts = document.querySelectorAll('script');
    problematicScripts.forEach(script => {
      if (script.src.includes('scripts.js')) {
        script.remove();
      }
    });
  }

  loadInitialData(): void {
    this.loadActivities();
  }

  loadActivities(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.activityService.getActivities(this.currentActivitiesPage)
      .subscribe({
        next: (response: any) => {
          this.isLoading = false;

          // Manejo de la respuesta según el formato
          let pageData: PageResponse<ActivityDto>;

          if (response && response.body) {
            // Si estamos recibiendo una HttpResponse completa
            pageData = response.body;
          } else if (response && response.content) {
            // Si estamos recibiendo directamente el objeto PageResponse
            pageData = response;
          } else {
            console.error('Response format is unexpected:', response);
            this.errorMessage = 'Error: Formato de respuesta inesperado';
            return;
          }

          if (pageData && Array.isArray(pageData.content)) {
            this.allActivities = [...this.allActivities, ...pageData.content];
            this.activitiesTotalPages = pageData.totalPages;
            this.hasMoreActivities = !pageData.last;
          } else {
            console.error('No content array in response:', pageData);
            this.errorMessage = 'Error: No se encontraron actividades';
          }
        },
        error: (err) => {
          console.error('Error loading activities:', err);
          this.isLoading = false;
          this.errorMessage = `Error: ${err.message || 'Error desconocido al cargar actividades'}`;
        }
      });
  }

  loadMoreActivities(): void {
    if (this.hasMoreActivities && !this.isLoading) {
      this.currentActivitiesPage++;
      this.loadActivities();
    }
  }

  navigateToActivity(id: number): void {
    this.router.navigate(['/activities', id]);
  }

  onLogin(): void {
    this.router.navigate(['/login']);
  }

  onLogout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.isLoggedIn = false;
        this.router.navigate(['/']).then(() => {
          window.location.reload();
        });
      },
      error: (err) => {
        console.error('Error during logout:', err);
      }
    });
  }
}
