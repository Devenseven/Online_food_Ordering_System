let currentPage = 0;
let currentKeyword = '';
let currentCategory = '';
let currentSort = '';

document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    updateNav(user);
    updateCartBadge();
    
    // Load categories for filter
    try {
        const categories = await apiRequest('/categories');
        const catSelect = document.getElementById('categoryFilter');
        categories.forEach(cat => {
            const opt = document.createElement('option');
            opt.value = cat.id;
            opt.textContent = cat.name;
            catSelect.appendChild(opt);
        });
    } catch (e) {
        console.error(e);
    }
    
    // Check URL params
    const params = new URLSearchParams(window.location.search);
    if (params.get('category')) {
        document.getElementById('categoryFilter').value = params.get('category');
        currentCategory = params.get('category');
    }
    if (params.get('search')) {
        document.getElementById('searchInput').value = params.get('search');
        currentKeyword = params.get('search');
    }
    
    document.getElementById('applyFilters').addEventListener('click', () => {
        currentKeyword = document.getElementById('searchInput').value.trim();
        currentCategory = document.getElementById('categoryFilter').value;
        currentSort = document.getElementById('sortSelect').value;
        currentPage = 0;
        loadFoods();
    });
    
    document.getElementById('searchInput').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            document.getElementById('applyFilters').click();
        }
    });
    
    loadFoods();
});

async function loadFoods() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('foodGrid').style.display = 'none';
    document.getElementById('emptyState').style.display = 'none';
    document.getElementById('pagination').innerHTML = '';
    
    try {
        let url = `/foods?page=${currentPage}&size=12`;
        if (currentKeyword) url += `&keyword=${encodeURIComponent(currentKeyword)}`;
        if (currentCategory) url += `&categoryId=${currentCategory}`;
        if (currentSort) url += `&sort=${currentSort}`;
        
        const data = await apiRequest(url);
        document.getElementById('loading').style.display = 'none';
        
        if (data.content.length === 0) {
            document.getElementById('emptyState').style.display = 'block';
            return;
        }
        
        const grid = document.getElementById('foodGrid');
        grid.style.display = 'grid';
        grid.innerHTML = data.content.map(food => `
            <div class="card">
                <img src="${food.imageUrl || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800'}" alt="${escapeHtml(food.name)}" style="width: 100%; height: 200px; object-fit: cover;">
                <div style="padding: 1rem;">
                    <h3 style="margin-bottom: 0.5rem; color: var(--text);">${escapeHtml(food.name)}</h3>
                    <p style="color: var(--text-light); font-size: 0.875rem; margin-bottom: 0.75rem; height: 42px; overflow: hidden;">${escapeHtml(food.description || '')}</p>
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem;">
                        <span style="font-size: 1.25rem; font-weight: 700; color: var(--primary);">${formatPrice(food.price)}</span>
                        <span class="badge ${food.available && food.active ? 'badge-delivered' : 'badge-cancelled'}">${food.available && food.active ? 'Available' : 'Unavailable'}</span>
                    </div>
                    <button class="btn" style="width: 100%;" onclick="addToCart(${food.id})" ${!food.available || !food.active ? 'disabled' : ''}>
                        ${!food.available || !food.active ? 'Unavailable' : 'Add to Cart'}
                    </button>
                </div>
            </div>
        `).join('');
        
        // Pagination
        const pagDiv = document.getElementById('pagination');
        for (let i = 0; i < data.totalPages; i++) {
            const btn = document.createElement('button');
            btn.className = i === data.page ? 'btn' : 'btn btn-secondary';
            btn.textContent = i + 1;
            btn.onclick = () => {
                currentPage = i;
                loadFoods();
                window.scrollTo({ top: 0, behavior: 'smooth' });
            };
            pagDiv.appendChild(btn);
        }
    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        showToast('Error loading menu', 'error');
    }
}

async function addToCart(foodId) {
    const user = await checkAuth();
    if (!user) {
        window.location.href = '/login.html?redirect=/menu.html';
        return;
    }
    try {
        await apiRequest('/cart/items', {
            method: 'POST',
            body: JSON.stringify({ foodItemId: foodId, quantity: 1 })
        });
        showToast('Added to cart!');
        updateCartBadge();
    } catch (e) {
        showToast(e.message || 'Failed to add to cart', 'error');
    }
}
