let page = 0;

async function loadMore() {
    page++;

    const response = await fetch(`/moreActivities?page=${page}`);
    const data = await response.text();

    document.getElementById('allActivitiesPaginated').insertAdjacentHTML('beforeend', data);
}