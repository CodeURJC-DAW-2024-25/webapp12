import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  registerForm: FormGroup;
  error: string | null = null;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      dni: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(4)]],
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.loading = true;
    this.error = null;

    const userData = {
      ...this.registerForm.value,
      roles: ['ROLE_USER']  // ← Rol explícito y correcto para Spring Security
    };

    this.authService.register(userData).subscribe({
      next: (res) => {
        if (res.status === 'ERROR') {
          this.error = 'No se pudo registrar el usuario';
        } else {
          this.router.navigate(['/']);
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error en el registro:', err);
        this.error = 'Error inesperado al registrar el usuario';
        this.loading = false;
      }
    });
  }
}
