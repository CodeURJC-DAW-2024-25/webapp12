import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from './../services/auth.service';
import { ActivityService, ActivityDto } from '../services/activity.service';
import { ReviewDto, ReviewService } from '../services/review.service';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.css']
})
export class ActivityComponent implements OnInit {
  activity: ActivityDto | null = null;
  imageUrl: string = '';
  reviews: ReviewDto[] = [];
  reviewsPage: number = 0;
  reviewsLastPage: boolean = false;
  reviewsLoading: boolean = false;
  token: string = 'ABC123XYZ';
  register: boolean = true;
  isSubscribed: boolean = false;

  constructor(
    private route: ActivatedRoute,
    public authService: AuthService,
    private activityService: ActivityService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (isNaN(id)) return;

    this.activityService.getActivityById(id).subscribe({
      next: (data) => {
        this.activity = data;
        if (data.id) {
          this.imageUrl = this.activityService.getActivityImageUrl(data.id);
          this.loadInitialReviews(data.id);
        }
      },
      error: (err) => {
        console.error('Error al cargar actividad', err);
      }
    });
  }

  loadMoreReview(): void {
    if (this.reviewsLastPage || !this.activity?.id || this.reviewsLoading) return;

    this.reviewsLoading = true;
    this.reviewsPage++;

    this.reviewService.getReviewsByActivity(this.activity.id, this.reviewsPage).subscribe({
      next: (response) => {
        console.log('Load more reviews response:', response);

        if (response && response.content && Array.isArray(response.content)) {
          this.reviews = [...this.reviews, ...response.content];
          this.reviewsLastPage = response.last;
        }

        this.reviewsLoading = false;
      },
      error: (e) => {
        console.error('Error al cargar más reviews:', e);
        this.reviewsLoading = false;
      }
    });
  }

  loadInitialReviews(activityId: number): void {
    this.reviewsLoading = true;

    this.reviewService.getReviewsByActivity(activityId, 0).subscribe({
      next: (response) => {
        console.log('Initial reviews response:', response);

        if (response && response.content && Array.isArray(response.content)) {
          this.reviews = response.content;
          this.reviewsLastPage = response.last;
          console.log('Reviews loaded:', this.reviews);
        } else {
          console.warn('Unexpected response format:', response);
          this.reviews = [];
          this.reviewsLastPage = true;
        }

        this.reviewsLoading = false;
      },
      error: (e) => {
        console.error('Error al cargar reviews:', e);
        this.reviews = [];
        this.reviewsLastPage = true;
        this.reviewsLoading = false;
      }
    });
  }

  submitReview(): void {
    if (!this.activity?.id) return;

    const starsValueElement = document.getElementById('starsValue') as HTMLSelectElement;
    const descriptionElement = document.getElementById('description') as HTMLTextAreaElement;

    if (!starsValueElement || !descriptionElement) return;

    // Crear objeto con los datos del formulario
    const review = {
      starsValue: parseInt(starsValueElement.value),
      description: descriptionElement.value // Mantenemos 'description' aquí para compatibilidad con el servicio
    };

    this.reviewService.submitReview(this.activity.id, review).subscribe({
      next: (response) => {
        // Añadimos la nueva review al principio de la lista
        if (response) {
          this.reviews.unshift(response);
        }

        // Limpiamos el formulario
        descriptionElement.value = '';
        starsValueElement.value = '5';

        window.location.reload();
      },
      error: (err) => {
        console.error('Error al enviar el comentario', err);
        alert('Error al enviar el comentario. Por favor, inténtelo de nuevo.');
      }
    });
  }
}
