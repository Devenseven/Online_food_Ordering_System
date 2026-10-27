document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user) {
        window.location.href = '/login.html?redirect=/orders.html';
        return;
    }
    updateNav(user);
    updateCartBadge();
    loadOrders();
});

async function loadOrders() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('ordersList').innerHTML = '';
    document.getElementById('emptyState').style.display = 'none';
    
    try {
        const orders = await apiRequest('/orders/my');
        document.getElementById('loading').style.display = 'none';
        
        if (orders.length === 0) {
            document.getElementById('emptyState').style.display = 'block';
            return;
        }
        
        const listDiv = document.getElementById('ordersList');
        listDiv.innerHTML = orders.map(order => `
            <div class="card" style="padding: 1.5rem;">
                <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.5rem;">
                    <div>
                        <h3 style="margin-bottom: 0.25rem;">Order #${order.id}</h3>
                        <p style="color: var(--text-light); font-size: 0.875rem;">${new Date(order.orderDate).toLocaleString()}</p>
                    </div>
                    <span class="badge badge-${order.status.toLowerCase()}">${order.status}</span>
                </div>
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.5rem;">
                    <span style="font-size: 1.1rem; font-weight: 700; color: var(--primary);">${formatPrice(order.totalAmount)}</span>
                    <span style="color: var(--text-light);">${order.items ? order.items.length : 0} item(s)</span>
                </div>
                <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                    <a href="/order-details.html?id=${order.id}" class="btn">View Details</a>
                    ${order.status === 'PLACED' ? `<button onclick="cancelOrder(${order.id})" class="btn btn-danger">Cancel Order</button>` : ''}
                </div>
            </div>
        `).join('');
    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        showToast('Error loading orders', 'error');
    }
}

async function cancelOrder(orderId) {
    if (!confirm('Cancel this order?')) return;
    try {
        await apiRequest(`/orders/${orderId}/cancel`, { method: 'POST' });
        showToast('Order cancelled');
        loadOrders();
    } catch (e) {
        showToast(e.message || 'Failed to cancel', 'error');
    }
}
