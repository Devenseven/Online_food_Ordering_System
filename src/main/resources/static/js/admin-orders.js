document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user || user.role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
    updateNav(user);
    loadOrders();
    
    document.getElementById('statusFilter').addEventListener('change', loadOrders);
});

async function loadOrders() {
    try {
        const status = document.getElementById('statusFilter').value;
        let url = '/admin/orders';
        if (status) url += '?status=' + status;
        const orders = await apiRequest(url);
        const tbody = document.getElementById('ordersTable');
        tbody.innerHTML = orders.map(order => `
            <tr>
                <td>#${order.id}</td>
                <td>${escapeHtml(order.userName || order.userEmail || '')}</td>
                <td>${new Date(order.orderDate).toLocaleDateString()}</td>
                <td>${formatPrice(order.totalAmount)}</td>
                <td><span class="badge badge-${order.status.toLowerCase()}">${order.status}</span></td>
                <td>
                    <select onchange="updateStatus(${order.id}, this.value)" class="form-input" style="width: auto; padding: 0.25rem 0.5rem;">
                        <option value="">Change Status</option>
                        ${getNextStatuses(order.status).map(s => `<option value="${s}">${s}</option>`).join('')}
                    </select>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        showToast('Error loading orders', 'error');
    }
}

function getNextStatuses(current) {
    const transitions = {
        'PLACED': ['CONFIRMED', 'CANCELLED'],
        'CONFIRMED': ['PREPARING', 'CANCELLED'],
        'PREPARING': ['OUT_FOR_DELIVERY'],
        'OUT_FOR_DELIVERY': ['DELIVERED'],
        'DELIVERED': [],
        'CANCELLED': []
    };
    return transitions[current] || [];
}

async function updateStatus(orderId, status) {
    if (!status) return;
    try {
        await apiRequest(`/admin/orders/${orderId}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status })
        });
        showToast('Status updated');
        loadOrders();
    } catch (e) {
        showToast(e.message || 'Failed to update status', 'error');
    }
}
