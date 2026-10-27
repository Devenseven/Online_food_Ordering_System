document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user) {
        window.location.href = '/login.html?redirect=/checkout.html';
        return;
    }
    updateNav(user);
    updateCartBadge();
    loadCheckout();
});

async function loadCheckout() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('checkoutContent').style.display = 'none';
    
    try {
        const cart = await apiRequest('/cart');
        document.getElementById('loading').style.display = 'none';
        
        if (!cart.items || cart.items.length === 0) {
            window.location.href = '/cart.html';
            return;
        }
        
        document.getElementById('checkoutContent').style.display = 'grid';
        
        const itemsDiv = document.getElementById('checkoutItems');
        itemsDiv.innerHTML = cart.items.map(item => `
            <div style="display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px solid var(--border);">
                <div>
                    <p style="font-weight: 600;">${escapeHtml(item.foodItemName)}</p>
                    <p style="font-size: 0.875rem; color: var(--text-light);">Qty: ${item.quantity}</p>
                </div>
                <span style="font-weight: 600;">${formatPrice(item.subtotal)}</span>
            </div>
        `).join('');
        
        document.getElementById('checkoutSubtotal').textContent = formatPrice(cart.cartTotal);
        document.getElementById('checkoutTotal').textContent = formatPrice(cart.cartTotal);
        
        document.getElementById('checkoutForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            document.getElementById('addressError').textContent = '';
            document.getElementById('contactError').textContent = '';
            
            const address = document.getElementById('address').value.trim();
            const contact = document.getElementById('contact').value.trim();
            const instructions = document.getElementById('instructions').value.trim();
            
            if (!address) {
                document.getElementById('addressError').textContent = 'Address is required';
                return;
            }
            if (!contact) {
                document.getElementById('contactError').textContent = 'Contact number is required';
                return;
            }
            
            const btn = document.getElementById('placeOrderBtn');
            btn.disabled = true;
            btn.textContent = 'Placing Order...';
            
            try {
                const order = await apiRequest('/orders', {
                    method: 'POST',
                    body: JSON.stringify({
                        deliveryAddress: address,
                        contactNumber: contact,
                        deliveryInstructions: instructions,
                        paymentMethod: 'COD'
                    })
                });
                window.location.href = '/order-confirmation.html?orderId=' + order.id;
            } catch (e) {
                btn.disabled = false;
                btn.textContent = 'Place Order';
                showToast(e.message || 'Failed to place order', 'error');
            }
        });
    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        if (e.message === 'Unauthorized') return;
        showToast('Error loading checkout', 'error');
    }
}
