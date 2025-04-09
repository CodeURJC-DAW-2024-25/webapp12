import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StatisticsService } from '../services/statistics.service';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';
import { Chart, registerables } from 'chart.js';

// Registrar los componentes necesarios de Chart.js
Chart.register(...registerables);

@Component({
  selector: 'app-admin-statistics',
  templateUrl: './admin-statistics.component.html',
  styleUrls: ['./admin-statistics.component.scss']
})
export class AdminStatisticsComponent implements OnInit {
  // Datos del usuario
  currentUser: any;
  isAdmin = false;
  isLoggedIn = false;

  // Estadísticas generales
  userCount: number = 0;
  activityCount: number = 0;
  placeCount: number = 0;

  // Datos para los gráficos
  activitiesByMonth: number[] = Array(12).fill(0);
  reviewsByValoration: number[] = Array(5).fill(0);

  // Referencias a los gráficos
  revenueChart: any;
  segmentsChart: any;

  constructor(
    private statisticsService: StatisticsService,
    public authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
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

    // Load statistics...
    this.loadGeneralStatistics();
    this.loadActivitiesByMonth();
    this.loadReviewStatistics();
  }

  loadGeneralStatistics(): void {
    this.statisticsService.getGeneralStatistics().subscribe({
      next: (data: { userCount: number; activityCount: number; placeCount: number; }) => {
        this.userCount = data.userCount;
        this.activityCount = data.activityCount;
        this.placeCount = data.placeCount;
      },
      error: (err: any) => {
        console.error('Error al cargar las estadísticas generales:', err);
      }
    });
  }

  loadActivitiesByMonth(): void {
    this.statisticsService.getActivitiesByMonth().subscribe({
      next: (data: any[]) => {
        // Inicializar array con ceros
        this.activitiesByMonth = Array(12).fill(0);

        // Actualizar valores según los datos recibidos
        data.forEach(item => {
          // Ajustar índice porque los meses van de 1-12 pero el array de 0-11
          if (item.month >= 1 && item.month <= 12) {
            this.activitiesByMonth[item.month - 1] = item.count;
          }
        });

        // Crear el gráfico de barras cuando tengamos los datos
        this.createRevenueChart();
      },
      error: (err: any) => {
        console.error('Error al cargar las actividades por mes:', err);
      }
    });
  }

  loadReviewStatistics(): void {
    this.statisticsService.getReviewStatistics().subscribe({
      next: (data: any[]) => {
        // Inicializar array con ceros
        this.reviewsByValoration = Array(5).fill(0);

        // Actualizar valores según los datos recibidos
        data.forEach(item => {
          // Las valoraciones van de 1-5
          if (item.valoration >= 1 && item.valoration <= 5) {
            this.reviewsByValoration[item.valoration - 1] = item.count;
          }
        });

        // Crear el gráfico circular cuando tengamos los datos
        this.createSegmentsChart();
      },
      error: (err: any) => {
        console.error('Error al cargar las estadísticas de valoraciones:', err);
      }
    });
  }

  createRevenueChart(): void {
    const ctx = document.getElementById('revenueChart') as HTMLCanvasElement;
    if (ctx) {
      this.revenueChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
          datasets: [{
            label: 'Cantidad de actividades realizadas durante el mes',
            data: this.activitiesByMonth,
            backgroundColor: '#2c4a7f'
          }]
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true
            }
          }
        }
      });
    }
  }

  createSegmentsChart(): void {
    const ctx = document.getElementById('segmentsChart') as HTMLCanvasElement;
    if (ctx) {
      this.segmentsChart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: ['1 estrella', '2 estrellas', '3 estrellas', '4 estrellas', '5 estrellas'],
          datasets: [{
            data: this.reviewsByValoration,
            backgroundColor: ['#e0f0ff', '#aad3ff', '#80b8ff', '#4f8cff', '#2c4a7f'],
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'bottom'
            }
          }
        }
      });
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
