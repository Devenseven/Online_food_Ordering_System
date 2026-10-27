document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    updateNav(user);
    updateCartBadge();
    
    // Load categories
    try {
        const categories = await apiRequest('/categories');
        const categoriesDiv = document.getElementById('categories');
        categoriesDiv.innerHTML = categories.map(cat => 
            `<a href="/menu.html?category=${cat.id}" class="btn" style="background: var(--white); color: var(--primary); border: 2px solid var(--primary);">${escapeHtml(cat.name)}</a>`
        ).join('');
    } catch (e) {
        console.error(e);
    }
    
    // Load popular items
    try {
        const data = await apiRequest('/foods?page=0&size=6');
        const itemsDiv = document.getElementById('popular-items');
        itemsDiv.innerHTML = data.content.map(food => `
            <div class="card" style="cursor: pointer;" onclick="window.location.href='/menu.html'">
                <img src="${food.imageUrl || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800'}" alt="${escapeHtml(food.name)}" style="width: 100%; height: 200px; object-fit: cover;">
                <div style="padding: 1rem;">
                    <h3 style="margin-bottom: 0.5rem; color: var(--text);">${escapeHtml(food.name)}</h3>
                    <p style="color: var(--text-light); font-size: 0.9rem; margin-bottom: 0.75rem; height: 40px; overflow: hidden;">${escapeHtml(food.description || '')}</p>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-size: 1.25rem; font-weight: 700; color: var(--primary);">${formatPrice(food.price)}</span>
                        <span class="badge ${food.available ? 'badge-delivered' : 'badge-cancelled'}">${food.available ? 'Available' : 'Unavailable'}</span>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (e) {
        console.error(e);
    }
});
