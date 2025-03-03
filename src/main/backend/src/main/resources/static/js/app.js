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
