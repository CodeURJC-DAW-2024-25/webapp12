let page = 0;

async function loadMore() {
    // Add logging to debug
    console.log('Load more function called');
    
    // Keep track of the current page
    const currentPage = document.getElementById('loadMore').getAttribute('data-page') || 0;
    const nextPage = parseInt(currentPage) + 1;
    console.log('Loading page:', nextPage);
    
    // Make AJAX request to get next page of activities
    fetch(`/?page=${nextPage}`, {
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        console.log('Response status:', response.status);
        return response.json();
    })
    .then(data => {
        console.log('Received data:', data);
        
        if (data.activities && data.activities.length > 0) {
            console.log('Activities found:', data.activities.length);
            
            // Get the container for activities
            const container = document.getElementById('allActivitiesPaginated');
            
            // Append new activities to the container
            data.activities.forEach(activity => {
                console.log('Adding activity:', activity.id, activity.name);
                
                const activityHtml = `
                <div class="col-sm-12 col-lg-3">
                    <div class="product-item bg-light">
                        <div class="card">
                            <div class="thumb-content">
                                <a href="/activity/${activity.id}">
                                    <img class="card-img-top img-fluid" src="/activity/${activity.id}/image" alt="Card image cap">
                                </a>
                            </div>
                            <div class="card-body">
                                <h4 class="card-title"><a href="/activity/${activity.id}">${activity.name}</a></h4>
                                <ul class="list-inline product-meta">
                                    <li class="list-inline-item">
                                        <i class="fa fa-folder-open-o"></i>${activity.category}
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>`;
                
                container.insertAdjacentHTML('beforeend', activityHtml);
            });
            
            // Update the current page
            document.getElementById('loadMore').setAttribute('data-page', nextPage);
            console.log('Updated page attribute to:', nextPage);
            
            // Hide "Load More" button if no more activities
            if (!data.hasMore) {
                console.log('No more activities, hiding button');
                document.getElementById('loadMore').style.display = 'none';
            }
        } else {
            // No more activities to load
            console.log('No activities returned, hiding button');
            document.getElementById('loadMore').style.display = 'none';
        }
    })
    .catch(error => {
        console.error('Error loading more activities:', error);
    });
}

// Initialize the page counter when the page loads
document.addEventListener('DOMContentLoaded', function() {
    console.log('Initializing load more button');
    document.getElementById('loadMore').setAttribute('data-page', '0');
});
let currentRecommendedPage = 0;

function loadMoreRecommended() {
    currentRecommendedPage++;
    console.log(`Loading recommended activities page ${currentRecommendedPage}`);
    
    // Fetch the next page as HTML
    fetch(`/?page=${currentRecommendedPage}&recommendedOnly=true`)
        .then(response => response.text())
        .then(html => {
            // Create a temporary DOM element to parse the HTML
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            
            // Extract the recommended activities from the response
            const newRecommendedActivities = doc.querySelectorAll('#recommendedActivitiesContainer > div');
            console.log(`Found ${newRecommendedActivities.length} new recommended activities`);
            
            if (newRecommendedActivities.length > 0) {
                // Add the new recommended activities to the current page
                const container = document.getElementById('recommendedActivitiesContainer');
                newRecommendedActivities.forEach(activity => {
                    container.appendChild(activity.cloneNode(true));
                });
            }
            
            // Check if there are more recommended activities to load
            const loadMoreButton = document.getElementById('loadMoreRecommended');
            const hasMoreButton = doc.querySelector('#loadMoreRecommended');
            
            if (!hasMoreButton) {
                console.log('No more recommended activities to load');
                if (loadMoreButton) {
                    loadMoreButton.style.display = 'none';
                }
            }
        })
        .catch(error => {
            console.error('Error loading more recommended activities:', error);
        });
}

async function loadMoreReview() {
    page++; 

    const loadMoreButton = document.getElementById("loadMore");
    const noMoreMessage = document.getElementById("noMoreReviewsMessage");

    if (!loadMoreButton) {
        console.error("❌ Error: El botón 'Cargar más' no existe en el DOM.");
        return;
    }

    const activityId = loadMoreButton.getAttribute("data-activity-id");

    const response = await fetch(`/moreReviews?activityId=${activityId}&page=${page}`);
    const data = await response.text();

    const reviewsContainer = document.getElementById("allReviewsPaginated");

    if (!reviewsContainer) {
        console.error("❌ Error: El contenedor de reseñas no existe en el DOM.");
        return;
    }

    reviewsContainer.insertAdjacentHTML("beforeend", data);

    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = data;

    const hasMore = tempDiv.querySelector("#loadMoreIndicator")?.getAttribute("data-has-more") === "true";

    if (!hasMore) {
        if (loadMoreButton) loadMoreButton.style.display = "none";
        if (noMoreMessage) noMoreMessage.style.display = "block";
    }
}

async function loadMoreUser() {
    page++;

    try {
        const response = await fetch(`/moreUsers?page=${page}`);
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: No se pudo cargar más usuarios.`);
        }

        const data = await response.text();

        // Agregar nuevos usuarios a la tabla
        document.querySelector('#allUsersPaginated tbody').insertAdjacentHTML('beforeend', data);

        // Crear un DOM temporal para analizar la respuesta
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = data;

        // Verificar si hay más usuarios
        const loadMoreIndicator = tempDiv.querySelector('#loadMoreIndicator');
        const hasMore = loadMoreIndicator && loadMoreIndicator.getAttribute('data-has-more') === 'true';

        // Si no hay más usuarios, ocultar el botón y mostrar mensaje
        if (!hasMore) {
            document.getElementById('loadMore').style.display = 'none';
            document.getElementById('noMoreUsersMessage').style.display = 'block';
        }
    } catch (error) {
        console.error("Error al cargar más usuarios:", error);
    }
}

let currentAdminPage = 0;

function loadMoreActivityAdmin() {
    currentAdminPage++;
    console.log(`Loading admin activities page ${currentAdminPage}`);
    
    fetch(`/adminActivities?page=${currentAdminPage}`)
        .then(response => response.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            
            const newActivities = doc.querySelectorAll('#activitiesTable tbody .activity-item');
            console.log(`Found ${newActivities.length} new activities`);
            
            if (newActivities.length > 0) {
                const container = document.querySelector('#activitiesTable tbody');
                newActivities.forEach(activity => {
                    container.appendChild(activity.cloneNode(true));
                });
            }
            
            // Actualiza la página actual y total
            const newCurrentPage = doc.getElementById('currentPage').value;
            const newTotalPages = doc.getElementById('totalPages').value;
            document.getElementById('currentPage').value = newCurrentPage;
            document.getElementById('totalPages').value = newTotalPages;
            
            // Oculta el botón si no hay más páginas
            if (currentAdminPage >= newTotalPages - 1) {
                document.getElementById('loadMore').style.display = 'none';
            }
        })
        .catch(error => {
            console.error('Error loading more activities:', error);
        });
}


async function loadMoreActivitiesSubscribed() {
    page++;

    try {
        const response = await fetch(`/moreActivitiesSubscribed?page=${page}`);
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: No se pudo cargar más actividades.`);
        }

        const data = await response.text();

        // Agregar nuevos actividades a la tabla
        document.querySelector('#allActivitiesPaginatedSubscribed tbody').insertAdjacentHTML('beforeend', data);

        // Crear un DOM temporal para analizar la respuesta
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = data;

        // Verificar si hay más actividades
        const loadMoreIndicator = tempDiv.querySelector('#loadMoreIndicator');
        const hasMore = loadMoreIndicator && loadMoreIndicator.getAttribute('data-has-more') === 'true';

        // Si no hay más actividades, ocultar el botón y mostrar mensaje
        if (!hasMore) {
            document.getElementById('loadMore').style.display = 'none';
            document.getElementById('noMoreSubscribedActivitysMessage').style.display = 'block';
        }
    } catch (error) {
        console.error("Error al cargar más actividades:", error);
    }
}


