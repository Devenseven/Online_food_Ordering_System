document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user) {
        window.location.href = '/login.html?redirect=/cart.html';
        return;
    }
    updateNav(user);
    updateCartBadge();
    loadCart();
});

async function loadCart() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('cartContent').style.display = 'none';
    document.getElementById('emptyCart').style.display = 'none';
    
    try {
        const cart = await apiRequest('/cart');
        document.getElementById('loading').style.display = 'none';
        
        if (!cart.items || cart.items.length === 0) {
            document.getElementById('emptyCart').style.display = 'block';
            updateCartBadge();
            return;
        }
        
        document.getElementById('cartContent').style.display = 'block';
        
        const itemsDiv = document.getElementById('cartItems');
        itemsDiv.innerHTML = cart.items.map(item => `
            <div class="card" style="display: flex; gap: 1rem; padding: 1rem; align-items: center;">
                <img src="${item.imageUrl || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800'}" alt="${escapeHtml(item.foodItemName)}" style="width: 100px; height: 100px; object-fit: cover; border-radius: 8px;">
                <div style="flex: 1;">
                    <h3 style="margin-bottom: 0.25rem;">${escapeHtml(item.foodItemName)}</h3>
                    <p style="color: var(--primary); font-weight: 600;">${formatPrice(item.unitPrice)}</p>
                    <p style="color: var(--text-light); font-size: 0.875rem;">${item.available && item.active ? 'Available' : 'Unavailable'}</p>
                </div>
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <button onclick="updateQuantity(${item.id}, ${item.quantity - 1})" class="btn" style="padding: 0.25rem 0.75rem;">-</button>
                    <span style="font-weight: 600; min-width: 30px; text-align: center;">${item.quantity}</span>
                    <button onclick="updateQuantity(${item.id}, ${item.quantity + 1})" class="btn" style="padding: 0.25rem 0.75rem;">+</button>
                </div>
                <div style="text-align: right; min-width: 80px;">
                    <p style="font-weight: 700; font-size: 1.1rem;">${formatPrice(item.subtotal)}</p>
                </div>
                <button onclick="removeItem(${item.id})" class="btn btn-danger" style="padding: 0.5rem 0.75rem;">Remove</button>
            </div>
        `).join('');
        
        document.getElementById('subtotal').textContent = formatPrice(cart.cartTotal);
        document.getElementById('total').textContent = formatPrice(cart.cartTotal);
        updateCartBadge();
    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        if (e.message === 'Unauthorized') return;
        showToast('Error loading cart', 'error');
    }
}

async function updateQuantity(cartItemId, quantity) {
    if (quantity < 1 || quantity > 20) return;
    try {
        await apiRequest(`/cart/items/${cartItemId}`, {
            method: 'PUT',
            body: JSON.stringify({ quantity })
        });
        loadCart();
        showToast('Cart updated');
    } catch (e) {
        showToast(e.message || 'Failed to update', 'error');
    }
}

async function removeItem(cartItemId) {
    try {
        await apiRequest(`/cart/items/${cartItemId}`, { method: 'DELETE' });
        loadCart();
        showToast('Item removed');
    } catch (e) {
        showToast(e.message || 'Failed to remove', 'error');
    }
}

async function clearCart() {
    if (!confirm('Clear all items from cart?')) return;
    try {
        await apiRequest('/cart', { method: 'DELETE' });
        loadCart();
        showToast('Cart cleared');
    } catch (e) {
        showToast(e.message || 'Failed to clear cart', 'error');
    }
}
