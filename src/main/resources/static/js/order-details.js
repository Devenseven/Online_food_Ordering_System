let orderIdGlobal;

document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user) {
        window.location.href = '/login.html';
        return;
    }
    updateNav(user);
    updateCartBadge();
    
    const params = new URLSearchParams(window.location.search);
    orderIdGlobal = params.get('id');
    
    if (!orderIdGlobal) {
        window.location.href = '/orders.html';
        return;
    }
    
    loadOrder();
});

async function loadOrder() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('orderContent').style.display = 'none';
    
    try {
        const order = await apiRequest('/orders/' + orderIdGlobal);
        document.getElementById('loading').style.display = 'none';
        document.getElementById('orderContent').style.display = 'block';
        
        document.getElementById('orderId').textContent = order.id;
        document.getElementById('orderDate').textContent = new Date(order.orderDate).toLocaleString();
        document.getElementById('paymentMethod').textContent = order.paymentMethod;
        document.getElementById('paymentStatus').textContent = order.paymentStatus;
        
        const statusEl = document.getElementById('orderStatus');
        statusEl.textContent = order.status;
        statusEl.className = 'badge badge-' + order.status.toLowerCase();
        
        document.getElementById('deliveryAddress').textContent = order.deliveryAddress;
        document.getElementById('contactNumber').textContent = order.contactNumber;
        document.getElementById('deliveryInstructions').textContent = order.deliveryInstructions || 'None';
        
        document.getElementById('subtotal').textContent = formatPrice(order.subtotal);
        document.getElementById('deliveryFee').textContent = formatPrice(order.deliveryFee);
        document.getElementById('total').textContent = formatPrice(order.totalAmount);
        
        const itemsDiv = document.getElementById('orderItems');
        itemsDiv.innerHTML = order.items.map(item => `
            <div style="display: flex; justify-content: space-between; padding: 0.75rem 0; border-bottom: 1px solid var(--border);">
                <div>
                    <p style="font-weight: 600;">${escapeHtml(item.itemName)}</p>
                    <p style="font-size: 0.875rem; color: var(--text-light);">${formatPrice(item.unitPrice)} x ${item.quantity}</p>
                </div>
                <span style="font-weight: 600;">${formatPrice(item.lineTotal)}</span>
            </div>
        `).join('');
        
        if (order.status === 'PLACED') {
            document.getElementById('cancelBtn').style.display = 'block';
        }
    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        showToast('Error loading order details', 'error');
    }
}

async function cancelOrder() {
    if (!confirm('Cancel this order?')) return;
    try {
        await apiRequest('/orders/' + orderIdGlobal + '/cancel', { method: 'POST' });
        showToast('Order cancelled');
        loadOrder();
    } catch (e) {
        showToast(e.message || 'Failed to cancel', 'error');
    }
}
