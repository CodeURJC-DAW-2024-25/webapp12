import { Component } from '@angular/core';
import { ActivityService, ActivityDto, PageResponse, } from '../services/activity.service';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { StatisticsService } from '../services/statistics.service';
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  allActivitiesSubscribed: ActivityDto[] = [];

  currentActivitiesPage = 0;
  activitiesTotalPages = 0;
  userCount:number = 0;
  activityCount:number = 0;
  subscribedActivitiesCount:number = 0;

  currentUser: any;

  isAdmin = false;
  isLoading = false;
  hasMoreActivities = true;
  isLoggedIn = false;

  errorMessage: string | null = null;
  userId: number | null = null;


  constructor(private activityService:ActivityService,public authService: AuthService,private router: Router,
  public statisticsService:StatisticsService,public userService: UserService){}

  ngOnInit():void{
    // Get current user from AuthService since that's what you're using for logout
    this.currentUser = this.authService.getUserDetails();
    console.log('Current user:', this.currentUser);

    // Use AuthService to check admin status
    this.isAdmin = this.authService.isAdmin();
    console.log('Is admin:', this.isAdmin);

    this.isLoggedIn = this.authService.getIsLoggedIn();
    console.log('Is logged in:', this.isLoggedIn);

    /// Verificar si es administrador
    this.isAdmin = this.authService.isAdmin();
    console.log('Is admin:', this.isAdmin);

    // Si no está logueado, redirigir al login
    if (!this.isLoggedIn) {
      console.error('Usuario no autenticado');
      this.router.navigate(['/login']);
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
        next:count =>{
          this.subscribedActivitiesCount = count;
        }
      });
    }
  }

  loadMoreActivitiesSubscribed(): void {
    if (this.hasMoreActivities && !this.isLoading) {
      this.currentActivitiesPage++;
      this.loadActivities();
    }
  }

  loadActivities(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.activityService.getUserSubscribedActivities(this.currentUser.id, this.currentActivitiesPage).subscribe(
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

            this.allActivitiesSubscribed = [...this.allActivitiesSubscribed, ...activitiesWithImages];
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

  navigateToActivity(id: number): void {
    this.router.navigate(['/activity', id]);
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/']);
    });
  }
}