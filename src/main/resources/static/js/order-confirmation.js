document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user) {
        window.location.href = '/login.html';
        return;
    }
    updateNav(user);
    updateCartBadge();
    
    const params = new URLSearchParams(window.location.search);
    const orderId = params.get('orderId');
    
    if (orderId) {
        try {
            const order = await apiRequest('/orders/' + orderId);
            document.getElementById('orderId').textContent = '#' + order.id;
            document.getElementById('orderTotal').textContent = formatPrice(order.totalAmount);
            const statusEl = document.getElementById('orderStatus');
            statusEl.textContent = order.status;
            statusEl.className = 'badge badge-' + order.status.toLowerCase();
        } catch (e) {
            document.getElementById('orderId').textContent = '#' + orderId;
        }
    }
});
