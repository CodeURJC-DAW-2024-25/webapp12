import { Component } from '@angular/core';
import { ActivityService, ActivityDto, PageResponse, } from '../services/activity.service';
import { AuthService } from '../services/auth.service';
import { HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { StatisticsService } from '../services/statistics.service';

@Component({
  selector: 'app-admin-activities',
  templateUrl: './admin-activities.component.html',
  styleUrl: './admin-activities.component.css'
})
export class AdminActivitiesComponent {
allActivities: ActivityDto[] = [];

  currentActivitiesPage = 0;
  activitiesTotalPages = 0;
  userCount: number = 0;
  activityCount: number = 0;
  subscribedActivitiesCount: number = 0;

  currentUser: any;

  isAdmin = false;
  isLoading = false;
  hasMoreActivities = true;
  isLoggedIn = false;

  errorMessage: string | null = null;
  userId: number | null = null;


  constructor(private activityService:ActivityService,public authService: AuthService,private router: Router,
    public statisticsService: StatisticsService){}

  ngOnInit():void{
    // Get current user from AuthService since that's what you're using for logout
    this.currentUser = this.authService.getUserDetails();
    console.log('Current user:', this.currentUser);

    // Use AuthService to check admin status
    this.isAdmin = this.authService.isAdmin();
    console.log('Is admin:', this.isAdmin);

    this.isLoggedIn = this.authService.getIsLoggedIn();
    console.log('Is logged in:', this.isLoggedIn);

    // If not admin, redirect
    if (!this.isAdmin) {
      console.error('El usuario no tiene permisos de administrador');
      this.router.navigate(['/']); // Redirect to home page
      return;
    }
    this.loadActivities();

    this.statisticsService.getGeneralStatistics().subscribe({
      next:data => {
        this.userCount = data.userCount;
        this.activityCount = data.activityCount; 
      }
    });
    if (this.currentUser?.id) {
      this.activityService.getUserSubscribedActivitiesCount(this.currentUser.id).subscribe({
        next: count => {
          this.subscribedActivitiesCount = count;
        }
      });
    }
  }

  deleteActivity(id:number):void{
    this.activityService.deleteActivity(id).subscribe({
      next:() => {
        this.allActivities = this.allActivities.filter(activity => activity.id !== id);
        this.activitiesTotalPages = this.activitiesTotalPages - 1;
        this.updateGeneralStatistics();
      },
      error: (error) => {
        console.error('Error al eliminar el usuario:', error);
        this.errorMessage = `Error: ${error.message || 'Error desconocido al eliminar el usuario'}`;
      }
    });
  }

  updateGeneralStatistics(): void {
    this.statisticsService.getGeneralStatistics().subscribe({
      next: data => {
        this.userCount = data.userCount;
        this.activityCount = data.activityCount; 
      },
      error: err => {
        console.error('Error al obtener estadísticas generales:', err);
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
    this.router.navigate(['/activity', id]);
  }

  loadActivities(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.activityService.getActivities(this.currentActivitiesPage).subscribe(
      {
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

  onLogout(): void {
    this.authService.logout().subscribe({
      next: () => {
        // No need to manually set isLoggedIn here as the service handles it
        // No need to navigate or reload as the service handles it
      },
      error: (err) => {
        console.error('Error during logout:', err);
      }
    });
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/']);
    });
  }
}