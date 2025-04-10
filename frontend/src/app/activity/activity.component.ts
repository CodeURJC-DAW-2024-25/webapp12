import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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
  token: string = 'ABC123XYZ';
  register: boolean = true;
  isSubscribed: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private activityService: ActivityService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (isNaN(id)) return;

    this.activityService.getActivityById(id).subscribe({
      next: (data) => {
        this.activity = data;
        this.imageUrl = this.activityService.getActivityImageUrl(data.id);
        this.loadInitialReviews(data.id);
      },
      error: (err) => {
        console.error('Error al cargar actividad', err);
      }
    });
  }

  loadMoreReview(): void {
    if (this.reviewsLastPage || !this.activity?.id) return;
    this.reviewsPage++;

    this.reviewService.getReviewsByActivity(this.activity.id, this.reviewsPage).subscribe({
      next: (page) => {
        this.reviews = [...this.reviews, ...page.content];
        this.reviewsLastPage = page.last;
      },
      error: (e) => console.error('Error al cargar más reviews:', e)
    });
  }

  loadInitialReviews(activityId: number): void {
    this.reviewService.getReviewsByActivity(activityId, this.reviewsPage).subscribe({
      next: (page) => {
        this.reviews = page.content;
        this.reviewsLastPage = page.last;
      },
      error: (e) => console.error('Error al cargar reviews:', e)
    });
  }

  submitReview() {
    alert('¡Comentario enviado!');
  }
}
