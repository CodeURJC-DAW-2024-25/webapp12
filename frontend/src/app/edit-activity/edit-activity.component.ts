import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService, ActivityDto, PlaceDto } from '../services/activity.service';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-edit-activity',
  templateUrl: './edit-activity.component.html',
  styleUrls: ['./edit-activity.component.css']
})
export class EditActivityComponent implements OnInit {
  activityForm: FormGroup;
  places: PlaceDto[] = [];
  activityId: number;
  activity: ActivityDto | null = null;
  selectedFile: File | null = null;
  removeCurrentImage: boolean = false;
  isLoading: boolean = true;
  isSubmitting: boolean = false;
  errorMessage: string | null = null;
  imageUrl: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private activityService: ActivityService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.activityId = 0;
    this.activityForm = this.createForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.activityId = +params['id'];
      this.loadActivityData();
      this.loadPlaces();
    });
  }

  createForm(): FormGroup {
    return this.formBuilder.group({
      name: ['', Validators.required],
      category: ['', Validators.required],
      description: ['', Validators.required],
      vacancy: [null, [Validators.required, Validators.min(1)]],
      placeId: ['', Validators.required],
      activityDate: ['', Validators.required],
      removeImage: [false]
    });
  }

  loadActivityData(): void {
    this.isLoading = true;
    this.activityService.getActivityById(this.activityId)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (activity) => {
          this.activity = activity;
          this.setFormValues(activity);

          if (activity.image) {
            this.imageUrl = this.activityService.getActivityImageUrl(activity.id);
          }
        },
        error: (error) => {
          console.error('Error al cargar la actividad:', error);
          this.errorMessage = 'No se pudo cargar la información de la actividad. Por favor, inténtalo de nuevo más tarde.';
        }
      });
  }

  setFormValues(activity: ActivityDto): void {
    let formattedDate = '';
    if (activity.activityDate) {
      const date = new Date(activity.activityDate);
      formattedDate = date.toISOString().split('T')[0];
    }

    this.activityForm.patchValue({
      name: activity.name,
      category: activity.category,
      description: activity.description,
      vacancy: activity.vacancy,
      placeId: activity.place?.id,
      activityDate: formattedDate,
      removeImage: false
    });
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
      this.activityForm.get('removeImage')?.setValue(false);
    }
  }

  onRemoveImageChange(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    this.removeCurrentImage = checkbox.checked;

    if (this.removeCurrentImage && this.selectedFile) {
      this.selectedFile = null;
      const fileInput = document.getElementById('file-upload') as HTMLInputElement;
      if (fileInput) {
        fileInput.value = '';
      }
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
      imageBoolean: this.activity?.image && !this.removeCurrentImage
    };

    this.activityService.updateActivity(this.activityId, activityData)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: (response) => {
          console.log('Actividad actualizada con éxito:', response);

          if (this.removeCurrentImage && this.activity?.image) {
            this.removeImage();
          }
          else if (this.selectedFile) {
            this.uploadImage();
          } else {
            this.navigateAfterSuccess();
          }
        },
        error: (error: HttpErrorResponse) => {
          this.handleError(error);
        }
      });
  }

  uploadImage(): void {
    if (!this.selectedFile) return;

    this.isSubmitting = true;
    this.activityService.updateActivityImage(this.activityId, this.selectedFile)
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

  removeImage(): void {
    this.isSubmitting = true;
    this.activityService.removeActivityImage(this.activityId)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: () => {
          console.log('Imagen eliminada con éxito');
          this.navigateAfterSuccess();
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al eliminar la imagen:', error);
          this.navigateAfterSuccess();
        }
      });
  }

  navigateAfterSuccess(): void {
    this.router.navigate(['/adminActivities']);
  }

  handleError(error: HttpErrorResponse): void {
    console.error('Error al actualizar la actividad:', error);
    if (error.status === 0) {
      this.errorMessage = 'No se pudo conectar con el servidor. Por favor, verifica tu conexión a Internet.';
    } else if (error.status === 400) {
      this.errorMessage = 'Los datos proporcionados no son válidos. Por favor, revisa el formulario.';
    } else {
      this.errorMessage = 'Ocurrió un error al actualizar la actividad. Por favor, inténtalo de nuevo más tarde.';
    }
  }

  onCancel(): void {
    this.router.navigate(['/adminActivities']);
  }
}
