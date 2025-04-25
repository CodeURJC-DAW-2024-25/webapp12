import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ActivityService, PlaceDto } from '../services/activity.service';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-create-activity',
  templateUrl: './create-activity.component.html',
  styleUrls: ['./create-activity.component.css']
})
export class CreateActivityComponent implements OnInit {
  activityForm: FormGroup;
  places: PlaceDto[] = [];
  selectedFile: File | null = null;
  isSubmitting = false;
  errorMessage: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private activityService: ActivityService,
    private router: Router
  ) {
    this.activityForm = this.formBuilder.group({
      name: ['', Validators.required],
      category: ['', Validators.required],
      description: ['', Validators.required],
      vacancy: [null, [Validators.required, Validators.min(1)]],
      placeId: ['', Validators.required],
      activityDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadPlaces();
  }

  loadPlaces(): void {
    this.activityService.getPlaces().subscribe({
      next: (data) => {
        this.places = data;
      },
      error: (error) => {
        console.error('Error al cargar los lugares:', error);
        this.errorMessage = 'No se pudieron cargar los lugares. Por favor, inténtalo de nuevo más tarde.';
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.activityForm.invalid) {
      Object.keys(this.activityForm.controls).forEach(key => {
        const control = this.activityForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    this.isSubmitting = true;

    const activityData = {
      name: this.activityForm.get('name')?.value,
      category: this.activityForm.get('category')?.value,
      description: this.activityForm.get('description')?.value,
      vacancy: this.activityForm.get('vacancy')?.value,
      placeId: this.activityForm.get('placeId')?.value,
      activityDate: this.activityForm.get('activityDate')?.value,
      image: this.selectedFile !== null
    };

    this.activityService.createActivity(activityData)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: (response) => {
          console.log('Actividad creada con éxito:', response);

          if (this.selectedFile && response.id) {
            this.uploadImage(response.id);
          } else {
            this.navigateAfterSuccess();
          }
        },
        error: (error: HttpErrorResponse) => {
          this.handleError(error);
        }
      });
  }

  uploadImage(activityId: number): void {
    if (!this.selectedFile) return;

    this.isSubmitting = true;
    this.activityService.uploadActivityImage(activityId, this.selectedFile)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: () => {
          console.log('Imagen subida con éxito');
          this.navigateAfterSuccess();
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al subir la imagen:', error);
          this.navigateAfterSuccess();
        }
      });
  }

  navigateAfterSuccess(): void {
    this.router.navigate(['/adminActivities']);
  }

  handleError(error: HttpErrorResponse): void {
    console.error('Error al crear la actividad:', error);
    if (error.status === 0) {
      this.errorMessage = 'No se pudo conectar con el servidor. Por favor, verifica tu conexión a Internet.';
    } else if (error.status === 400) {
      this.errorMessage = 'Los datos proporcionados no son válidos. Por favor, revisa el formulario.';
    } else {
      this.errorMessage = 'Ocurrió un error al crear la actividad. Por favor, inténtalo de nuevo más tarde.';
    }
  }
}
