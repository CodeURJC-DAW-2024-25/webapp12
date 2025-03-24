
document.addEventListener("DOMContentLoaded", function () {

    const revenueCtx = document.getElementById('revenueChart').getContext('2d');
    new Chart(revenueCtx, {
        type: 'bar',
        data: {
            labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
            datasets: [{
                label: 'Cantidad de actividades realizadas durante el mes',
                data: [10, 2, 30, 40, 5, 60, 1, 20, 3, 4, 50, 6],
                backgroundColor: '#2c4a7f' 
            }]
        },
        options: { responsive: true }
    });

    const segmentsCtx = document.getElementById('segmentsChart').getContext('2d');
    new Chart(segmentsCtx, {
        type: 'pie',
        data: {
            labels: ['1', '2', '3', '4', '5'],
            datasets: [{
                data: [2, 5, 7, 1, 12],
                backgroundColor: ['#e0f0ff', '#aad3ff', '#80b8ff', '#4f8cff', '#2c4a7f'],
            }]
        },
        options: { responsive: true }
    });
});
