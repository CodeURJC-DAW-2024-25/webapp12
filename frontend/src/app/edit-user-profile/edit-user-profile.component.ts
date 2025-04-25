import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { UserDto } from '../dtos/user.dto';
import { NgForm } from '@angular/forms';
import { StatisticsService } from '../services/statistics.service';
import { ActivityService } from '../services/activity.service';

@Component({
  selector: 'app-edit-user-profile',
  templateUrl: './edit-user-profile.component.html',
  styleUrl: './edit-user-profile.component.css'
})


export class EditUserProfileComponent {
  currentUser: UserDto | null = null;

  isAdmin = false;
  isLoggedIn = false;
  removeImage: boolean = false;

  errorMessage: string | null = null;
  userId: number | null = null;
  selectedImage: File | null = null;

  userCount: number = 0;
  activityCount: number = 0;
  subscribedActivitiesCount: number = 0;
  

  name: string = '';
  surname: string = '';
  dni: string = '';
  tlf: string = '';

  constructor(public userService:UserService,public authService: AuthService,private router: Router,
    public statisticsService: StatisticsService,public activityService:ActivityService
  ){}
  
  ngOnInit():void{
    // Get current user from AuthService since that's what you're using for logout
    this.currentUser = this.authService.getUserDetails();
    console.log('Current user:', this.currentUser);

    // Use AuthService to check admin status
    this.isAdmin = this.authService.isAdmin();
    console.log('Is admin:', this.isAdmin);

    this.isLoggedIn = this.authService.getIsLoggedIn();
    console.log('Is logged in:', this.isLoggedIn);

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
    if (this.currentUser) {
      this.name = this.currentUser.name;
      this.surname = this.currentUser.surname;
      this.dni = this.currentUser.dni;
      this.tlf = this.currentUser.phone;
    }
  }

  onImageSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedImage = fileInput.files[0];
      console.log('Imagen seleccionada:', this.selectedImage);
    }
  }

  onSubmit(): void {
    if (this.currentUser) {
      const updatedUser: UserDto = {
        id: this.currentUser.id,
        name: this.name,
        surname: this.surname,
        dni: this.dni,
        phone: this.tlf,
        image: this.currentUser.image,
        email: this.currentUser.email,
        role: this.currentUser.role,
      };
      console.log('Enviando al backend:', updatedUser);
  
      this.userService.updateUser(updatedUser.id, updatedUser).subscribe({
        next: (response) => {
          console.log('Usuario actualizado correctamente', response);
          
          this.authService.updateUserDetails(response);
          
          if (this.selectedImage) {
            console.log('Subiendo imagen...');
            this.updateUserImage();
          } else if (this.removeImage) {
            this.onRemoveImage();
          } else {
            this.router.navigate(['/profile']);
          }
        },
        error: (err) => {
          this.errorMessage = 'Error al actualizar los datos del usuario: ' + err;
        }
      });
    }
  }
  
  updateUserImage(): void {
    if (this.selectedImage && this.currentUser) {
      const formData = new FormData();
      formData.append('file', this.selectedImage);
  
      this.userService.updateUserImage(this.currentUser.id, formData).subscribe({
        next: (response) => {
          console.log('Imagen actualizada correctamente');
          
          this.userService.getUserById(this.currentUser!.id).subscribe({
            next: (updatedUser) => {
              this.authService.updateUserDetails(updatedUser);
              this.router.navigate(['/profile']);
            }
          });
        },
        error: (err) => {
          this.errorMessage = 'Error al actualizar la imagen del usuario: ' + err;
        }
      });
    }
  }
  
  onRemoveImage(): void {
    if (this.currentUser) {
      this.userService.deleteUserImage(this.currentUser.id).subscribe({
        next: () => {
          console.log('Imagen eliminada correctamente');
          
          this.userService.getUserById(this.currentUser!.id).subscribe({
            next: (updatedUser) => {
              this.authService.updateUserDetails(updatedUser);
              this.router.navigate(['/profile']);
            }
          });
        },
        error: (err) => {
          this.errorMessage = 'Error al eliminar la imagen: ' + err;
        }
      });
    }
  }
  
  getProfileImageUrl(): string {
    return this.currentUser && this.currentUser.image
      ? `/user/${this.currentUser.id}/image`
      : '/images/sports/no-image.png';
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