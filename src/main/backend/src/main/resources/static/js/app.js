let page = 0;

async function loadMore() {
    page++;

    const response = await fetch(`/moreActivities?page=${page}`);
    const data = await response.text();

    document.getElementById('allActivitiesPaginated').insertAdjacentHTML('beforeend', data);
    // Crear un DOM temporal para analizar la respuesta
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = data;

    // Verificar si hay más elementos en la paginación
    const hasMore = tempDiv.querySelector('#loadMoreIndicator')?.getAttribute('data-has-more') === 'true';

    // Si no hay más actividades, ocultar el botón y mostrar el mensaje
    if (!hasMore) {
        document.getElementById('loadMore').style.display = 'none';
        document.getElementById('noMoreActivitiesMessage').style.display = 'block';
    }


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
