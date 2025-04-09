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
  isLoadingRecommended = false;
  isLoggedIn = false;
  errorMessage: string | null = null;
  userId: number | null = null;

  constructor(
    private activityService: ActivityService,
    public authService: AuthService,
    private router: Router
  ) {
    // Actualizar estado de login y userId al inicializar
    this.updateLoginStatus();
  }

  // Método para actualizar el estado de login
  private updateLoginStatus(): void {
    // Verificar si el usuario está logueado usando AuthService
    this.isLoggedIn = this.authService.getIsLoggedIn();
    console.log('Estado de login actualizado:', this.isLoggedIn);

    // Intentar obtener userId
    if (this.isLoggedIn) {
      this.userId = this.authService.getCurrentUserId();
      console.log('ID de usuario obtenido:', this.userId);
    }
  }

  ngOnInit(): void {
    // Esperar un poco antes de cargar datos para asegurar que las cookies estén disponibles
    setTimeout(() => {
      this.updateLoginStatus(); // Actualizar de nuevo por si acaso
      this.loadInitialData();
    }, 500);

    this.removeProblematicScript();
  }

  private removeProblematicScript(): void {
    const problematicScripts = document.querySelectorAll('script');
    problematicScripts.forEach(script => {
      if (script.src.includes('scripts.js')) {
        script.remove();
      }
    });
  }

  loadInitialData(): void {
    console.log('Cargando datos iniciales. Usuario logueado:', this.isLoggedIn);

    // Cargar actividades recomendadas si el usuario está logueado
    if (this.isLoggedIn) {
      console.log('Intentando cargar actividades recomendadas...');
      this.loadRecommendedActivities();
    } else {
      console.log('Usuario no logueado, no se cargan recomendaciones');
    }

    // Cargar todas las actividades
    this.loadActivities();
  }

  loadRecommendedActivities(): void {
    if (this.isLoadingRecommended) {
      return;
    }

    // Asegurarse de tener userId para la solicitud
    const userId = this.userId || 1; // Usar 1 como fallback solo para pruebas

    this.isLoadingRecommended = true;
    console.log('Solicitando actividades recomendadas para el usuario:', userId);

    // Probar el método principal primero
    this.activityService.getRecommendedActivities(userId, this.currentRecommendedPage)
      .subscribe({
        next: (response: any) => {
          this.handleRecommendedActivitiesResponse(response);
        },
        error: (err) => {
          console.error('Error con ruta principal, intentando ruta alternativa:', err);

          // Si falla, intentar con la ruta alternativa
          this.activityService.getRecommendedActivitiesAlternative(userId, this.currentRecommendedPage)
            .subscribe({
              next: (response: any) => {
                this.handleRecommendedActivitiesResponse(response);
              },
              error: (errAlt) => {
                console.error('Error al cargar actividades recomendadas (también en ruta alternativa):', errAlt);
                this.isLoadingRecommended = false;
              }
            });
        }
      });
  }

  private handleRecommendedActivitiesResponse(response: any): void {
    this.isLoadingRecommended = false;
    console.log('Respuesta de actividades recomendadas recibida:', response);

    // Manejo de la respuesta según el formato
    let pageData: any;

    if (response && response.body) {
      pageData = response.body;
      console.log('Usando response.body');
    } else if (response && response.content) {
      pageData = response;
      console.log('Usando response directamente');
    } else {
      pageData = response;
      console.log('Formato de respuesta no reconocido, intentando usar la respuesta completa');
    }

    if (pageData && Array.isArray(pageData.content)) {
      console.log('Contenido de actividades recomendadas:', pageData.content);
      this.recommendedActivities = [...this.recommendedActivities, ...pageData.content];
      this.recommendedTotalPages = pageData.totalPages || 1;
      this.hasMoreRecommended = pageData.last === false;
      console.log('Actividades recomendadas cargadas:', this.recommendedActivities.length);
    } else if (Array.isArray(pageData)) {
      // Si la respuesta es directamente un array
      console.log('La respuesta es un array directo');
      this.recommendedActivities = [...this.recommendedActivities, ...pageData];
      this.hasMoreRecommended = false;
      console.log('Actividades recomendadas cargadas:', this.recommendedActivities.length);
    } else {
      console.error('No se pudo extraer contenido de actividades recomendadas:', pageData);
    }
  }

  loadMoreRecommendedActivities(): void {
    if (this.hasMoreRecommended && !this.isLoadingRecommended) {
      this.currentRecommendedPage++;
      this.loadRecommendedActivities();
    }
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
