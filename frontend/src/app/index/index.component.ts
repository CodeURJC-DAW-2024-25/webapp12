import { Component, OnInit } from '@angular/core';
import { ActivityService, ActivityDto, PageResponse, PlaceDto } from '../services/activity.service';
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
  searchResults: ActivityDto[] = [];
  places: PlaceDto[] = [];
  selectedPlaceId: number | null = null;

  // Propiedades para paginación
  currentRecommendedPage = 0;
  currentActivitiesPage = 0;
  currentSearchPage = 0;
  recommendedTotalPages = 0;
  activitiesTotalPages = 0;
  searchTotalPages = 0;

  // Flags
  hasMoreRecommended = true;
  hasMoreActivities = true;
  hasMoreSearchResults = true;
  isLoading = false;
  isLoadingRecommended = false;
  isSearching = false;
  isLoggedIn = false;
  hasSearched = false;

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
      this.loadPlaces(); // Cargar la lista de lugares disponibles
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

  // Método para cargar los lugares disponibles
  loadPlaces(): void {
    this.activityService.getPlaces().subscribe({
      next: (places) => {
        this.places = places;
        console.log('Lugares cargados:', places.length);
      },
      error: (err) => {
        console.error('Error al cargar lugares:', err);
      }
    });
  }

  loadRecommendedActivities(): void {

    if (this.isLoadingRecommended) {
      return;
    }

    // Asegurarse de tener userId para la solicitud
     // Usar 1 como fallback solo para pruebas
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.log('No hay usuario autenticado, no se cargan actividades recomendadas');
      return;
    }

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

      // Set image URLs for each recommended activity
      const recommendedWithImages = pageData.content.map((activity: ActivityDto) => ({
        ...activity,
        imageUrl: this.activityService.getActivityImageUrl(activity.id)
      }));

      this.recommendedActivities = [...this.recommendedActivities, ...recommendedWithImages];
      this.recommendedTotalPages = pageData.totalPages || 1;
      this.hasMoreRecommended = pageData.last === false;
      console.log('Actividades recomendadas cargadas:', this.recommendedActivities.length);
    } else if (Array.isArray(pageData)) {
      // Si la respuesta es directamente un array
      console.log('La respuesta es un array directo');

      // Set image URLs for each recommended activity
      const recommendedWithImages = pageData.map((activity: ActivityDto) => ({
        ...activity,
        imageUrl: this.activityService.getActivityImageUrl(activity.id)
      }));

      this.recommendedActivities = [...this.recommendedActivities, ...recommendedWithImages];
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

  loadMoreActivities(): void {
    if (this.hasMoreActivities && !this.isLoading) {
      this.currentActivitiesPage++;
      this.loadActivities();
    }
  }

  navigateToActivity(id: number): void {
    this.router.navigate(['/activity', id]);
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

  // Modify your loadActivities method in IndexComponent
  loadActivities(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.activityService.getActivities(this.currentActivitiesPage)
      .subscribe({
        next: (response: any) => {
          this.isLoading = false;

          let pageData: PageResponse<ActivityDto>;

          if (response && response.body) {
            pageData = response.body;
          } else if (response && response.content) {
            pageData = response;
          } else {
            console.error('Response format is unexpected:', response);
            this.errorMessage = 'Error: Formato de respuesta inesperado';
            return;
          }

          if (pageData && Array.isArray(pageData.content)) {
            // Set image URLs for each activity
            const activitiesWithImages = pageData.content.map((activity: ActivityDto) => ({
              ...activity,
              imageUrl: this.activityService.getActivityImageUrl(activity.id)
            }));

            this.allActivities = [...this.allActivities, ...activitiesWithImages];
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

  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    if (imgElement) {
      imgElement.src = '/images/sports/no-image.png';
    }
  }

  searchActivitiesByPlace(): void {
    if (!this.selectedPlaceId) {
      return;
    }

    this.hasSearched = true;
    this.isSearching = true;
    this.searchResults = [];
    this.currentSearchPage = 0;

    this.performSearch();
  }

  private performSearch(): void {
    if (!this.selectedPlaceId) {
      return;
    }

    this.activityService.searchActivitiesByPlace(this.selectedPlaceId, this.currentSearchPage).subscribe({
      next: (response: any) => {
        this.isSearching = false;

        let pageData: PageResponse<ActivityDto>;

        if (response && response.body) {
          pageData = response.body;
        } else if (response && response.content) {
          pageData = response;
        } else {
          console.error('Formato de respuesta inesperado:', response);
          return;
        }

        if (pageData && Array.isArray(pageData.content)) {
          const activitiesWithImages = pageData.content.map((activity: ActivityDto) => ({
            ...activity,
            imageUrl: this.activityService.getActivityImageUrl(activity.id)
          }));

          this.searchResults = [...this.searchResults, ...activitiesWithImages];
          this.searchTotalPages = pageData.totalPages;
          this.hasMoreSearchResults = !pageData.last;
        } else {
          console.error('No se encontró contenido en la respuesta:', pageData);
        }
      },
      error: (err) => {
        this.isSearching = false;
        console.error('Error al buscar actividades:', err);
      }
    });
  }

  loadMoreSearchResults(): void {
    if (this.hasMoreSearchResults && !this.isSearching) {
      this.currentSearchPage++;
      this.performSearch();
    }
  }
}
