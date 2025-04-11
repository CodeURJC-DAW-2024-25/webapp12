import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { UserDto } from '../dtos/user.dto';
@Component({
  selector: 'app-edit-user-profile',
  templateUrl: './edit-user-profile.component.html',
  styleUrl: './edit-user-profile.component.css'
})


export class EditUserProfileComponent {
  currentUser: UserDto | undefined;

  isAdmin = false;
  isLoggedIn = false;

  errorMessage: string | null = null;
  userId: number | null = null;
  selectedImage: File | null = null;

  name: string = '';
  surname: string = '';
  dni: string = '';
  phone: string = '';

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

    if (this.currentUser) {
      this.name = this.currentUser.name;
      this.surname = this.currentUser.surname;
      this.dni = this.currentUser.dni;
      this.phone = this.currentUser.phone;
    }
    
  }

  onImageSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedImage = fileInput.files[0];
    }
  }

  onSubmit(): void {
    if (this.currentUser) {
      const updatedUser: UserDto = {
        id: this.currentUser.id,
        name: this.name,
        surname: this.surname,
        dni: this.dni,
        phone: this.phone,
        image: this.currentUser.image,  
        email: this.currentUser.email, 
        role: this.currentUser.role,
      };

      this.userService.updateUser(updatedUser.id, updatedUser).subscribe({
        next: () => {
          console.log('Usuario actualizado correctamente');
          this.router.navigate(['/profilePage']);
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
      formData.append('image', this.selectedImage);

      this.userService.updateUserImage(this.currentUser.id, formData).subscribe({
        next: () => {
          console.log('Imagen actualizada correctamente');
          window.location.reload(); // Reload the page to reflect the updated image
        },
        error: (err) => {
          this.errorMessage = 'Error al actualizar la imagen del usuario: ' + err;
        }
      });
    }
  }

  removeImage(): void {
    if (this.currentUser) {
      this.userService.deleteUserImage(this.currentUser.id).subscribe({
        next: () => window.location.reload(),
        error: () => this.errorMessage = 'Error al eliminar la imagen.'
      });
    } else {
      console.error('No se encontró al usuario actual');
      this.errorMessage = 'No se encontró al usuario actual';
    }
  }
  
  getProfileImageUrl(): string {
    return this.currentUser && this.currentUser.image
      ? `/user/${this.currentUser.id}/image`
      : '/images/sports/no-image.png';
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
  

