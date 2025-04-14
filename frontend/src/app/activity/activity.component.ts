import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
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
  isSubscribed: boolean = false;
  reservationLoading: boolean = false;
  downloadingTicket: boolean = false;

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
          this.checkSubscriptionStatus(data.id);
        }
      },
      error: (err) => {
        console.error('Error al cargar actividad', err);
      }
    });
  }

  
  // Método para verificar si el usuario ya está inscrito
  checkSubscriptionStatus(activityId: number): void {
    if (!this.authService.getIsLoggedIn()) return;

    this.activityService.isUserSubscribed(activityId).subscribe({
      next: (subscribed) => {
        this.isSubscribed = subscribed;
        console.log('Estado de suscripción:', this.isSubscribed);
      },
      error: (err) => {
        console.error('Error al verificar estado de suscripción', err);
        this.isSubscribed = false;
      }
    });
  }

  reserveActivity(): void {
    if (!this.activity?.id || this.reservationLoading || this.isSubscribed) return;

    this.reservationLoading = true;

    this.activityService.reserveActivity(this.activity.id).subscribe({
      next: (pdfBlob) => {
        // Crear URL para descargar el PDF
        const url = window.URL.createObjectURL(pdfBlob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Ticket_Reserva_Actividad_${this.activity?.id}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        // Actualizar estado de suscripción
        this.isSubscribed = true;
        this.reservationLoading = false;
      },
      error: (err) => {
        console.error('Error al realizar la reserva', err);
        let errorMessage = 'Error al realizar la reserva. Por favor, inténtelo de nuevo.';

        if (err.status === 400) {
          errorMessage = 'No hay plazas disponibles para esta actividad.';
        } else if (err.status === 401) {
          errorMessage = 'Debe iniciar sesión para reservar una actividad.';
        }

        alert(errorMessage);
        this.reservationLoading = false;
      }
    });
  }

  // Nuevo método para descargar el ticket de una reserva existente
  downloadTicket(): void {
    if (!this.activity?.id || this.downloadingTicket) return;

    this.downloadingTicket = true;

    try {
      this.activityService.downloadReservationPdf(this.activity.id).subscribe({
        next: (pdfBlob) => {
          const userName = this.authService.getUserDetails()?.name || 'Usuario';
          this.downloadPdf(pdfBlob, `Ticket_Reserva_${userName}.pdf`);
          this.downloadingTicket = false;
        },
        error: (err) => {
          console.error('Error al descargar el ticket', err);
          let errorMessage = 'Error al descargar el ticket. Por favor, inténtelo de nuevo.';

          if (err.status === 400) {
            errorMessage = 'No estás inscrito en esta actividad.';
          } else if (err.status === 404) {
            errorMessage = 'Actividad o usuario no encontrado.';
          }

          alert(errorMessage);
          this.downloadingTicket = false;
        }
      });
    } catch (error) {
      console.error('Error:', error);
      alert('Error: Debes iniciar sesión para descargar el ticket.');
      this.downloadingTicket = false;
    }
  }

  // Método auxiliar para descargar PDF
  private downloadPdf(pdfBlob: Blob, fileName: string): void {
    const url = window.URL.createObjectURL(pdfBlob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
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
