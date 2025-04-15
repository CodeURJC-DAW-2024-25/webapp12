import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { UserService,PageResponse } from '../services/user.service';
import { UserDto } from '../dtos/user.dto';
import { HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css'
})
export class AdminUsersComponent {
  allUsersPaginated: UserDto[] = [];
  
    currentUsersPage = 0;
    usersTotalPages = 0;
  
    currentUser: any;
  
    isAdmin = false;
    isLoading = false;
    hasMoreUsers = true;
    isLoggedIn = false;
  
    errorMessage: string | null = null;
    userId: number | null = null;
  
  
    constructor(private userService:UserService,public authService: AuthService,private router: Router){}
  
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
      this.loadUsers();
    }
  
    loadMoreUsers(): void {
      if (this.hasMoreUsers && !this.isLoading) {
        this.currentUsersPage++;
        this.loadUsers();
      }
    }
  
    deleteUser(id:number): void {
      this.userService.deleteUser(id).subscribe({
        next:() => {
          this.allUsersPaginated = this.allUsersPaginated.filter(user => user.id !== id);
          this.usersTotalPages = this.usersTotalPages - 1;
        },
        error: (error) => {
          console.error('Error al eliminar el usuario:', error);
          this.errorMessage = `Error: ${error.message || 'Error desconocido al eliminar el usuario'}`;
        }
      })
    }

    navigateToUser(id: number): void {
      this.router.navigate(['/users', id]);
    }
  
    loadUsers(): void {
      if (this.isLoading) return;
  
      this.isLoading = true;
      this.errorMessage = null;
  
      this.userService.getUsers(this.currentUsersPage).subscribe(
        {
          next: (response: any) => {
            this.isLoading = false;
  
            let pageData: PageResponse<UserDto>;
  
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
              const userWithImages = pageData.content.map((user: UserDto) => ({
                ...user,
                imageUrl: this.userService.getUserImageUrl(user.id)
              }));
  
              this.allUsersPaginated = [...this.allUsersPaginated, ...userWithImages];
              this.usersTotalPages = pageData.totalPages;
              this.hasMoreUsers = !pageData.last;
            } else {
              console.error('No content array in response:', pageData);
              this.errorMessage = 'Error: No se encontraron usuarios';
            }
          },
          error: (err) => {
            console.error('Error loading usuarios:', err);
            this.isLoading = false;
            this.errorMessage = `Error: ${err.message || 'Error desconocido al cargar usuarios'}`;
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
