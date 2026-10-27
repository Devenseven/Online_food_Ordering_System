document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user || user.role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
    updateNav(user);
    
    try {
        const dashboard = await apiRequest('/admin/dashboard');
        document.getElementById('loading').style.display = 'none';
        document.getElementById('dashboardContent').style.display = 'block';
        
        document.getElementById('totalOrders').textContent = dashboard.totalOrders;
        document.getElementById('activeFoods').textContent = dashboard.activeFoodItems;
        document.getElementById('totalSales').textContent = formatPrice(dashboard.totalSales);
        
        const statusDiv = document.getElementById('ordersByStatus');
        for (const [status, count] of Object.entries(dashboard.ordersPerStatus)) {
            statusDiv.innerHTML += `
                <div style="padding: 0.75rem 1.25rem; background: var(--bg-cream); border-radius: 8px;">
                    <span class="badge badge-${status.toLowerCase()}">${status}</span>
                    <strong style="margin-left: 0.5rem;">${count}</strong>
                </div>
            `;
        }
    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        showToast('Error loading dashboard', 'error');
    }
});
