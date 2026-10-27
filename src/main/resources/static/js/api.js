const API_BASE = '/api';

function getCsrfToken() {
    const name = 'XSRF-TOKEN=';
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookieArray = decodedCookie.split(';');
    for (let i = 0; i < cookieArray.length; i++) {
        let cookie = cookieArray[i];
        while (cookie.charAt(0) === ' ') {
            cookie = cookie.substring(1);
        }
        if (cookie.indexOf(name) === 0) {
            return cookie.substring(name.length, cookie.length);
        }
    }
    return '';
}

async function apiRequest(endpoint, options = {}) {
    const defaultOptions = {
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json',
        }
    };
    
    const method = options.method || 'GET';
    if (method !== 'GET' && method !== 'HEAD') {
        defaultOptions.headers['X-XSRF-TOKEN'] = getCsrfToken();
    }
    
    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...(options.headers || {})
        }
    };
    
    try {
        const response = await fetch(API_BASE + endpoint, finalOptions);
        
        if (response.status === 401) {
            window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
            return Promise.reject(new Error('Unauthorized'));
        }
        
        if (response.status === 204) {
            return null;
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            if (!response.ok) {
                const error = new Error(data.message || 'An error occurred');
                error.data = data;
                throw error;
            }
            return data;
        }
        
        if (!response.ok) {
            throw new Error('An error occurred');
        }
        
        return null;
    } catch (error) {
        throw error;
    }
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.textContent = message;
    toast.className = 'toast show ' + type;
    setTimeout(() => {
        toast.className = 'toast';
    }, 3000);
}

function escapeHtml(text) {
    if (text === null || text === undefined) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatPrice(price) {
    return '$' + parseFloat(price).toFixed(2);
}

async function checkAuth() {
    try {
        const user = await apiRequest('/auth/me');
        return user;
    } catch {
        return null;
    }
}

function updateNav(user) {
    const navAuth = document.getElementById('nav-auth');
    if (!navAuth) return;
    
    if (user) {
        navAuth.innerHTML = `
            <span style="color: var(--text-light);">Hello, ${escapeHtml(user.fullName)}</span>
            ${user.role === 'ADMIN' ? '<a href="/admin/dashboard.html" class="nav-link">Admin</a>' : ''}
            ${user.role === 'CUSTOMER' ? '<a href="/orders.html" class="nav-link">My Orders</a>' : ''}
            <button onclick="logout()" class="nav-btn" style="background: var(--text-light);">Logout</button>
        `;
    } else {
        navAuth.innerHTML = `
            <a href="/login.html" class="nav-link">Login</a>
            <a href="/register.html" class="nav-btn">Register</a>
        `;
    }
}

async function logout() {
    try {
        await apiRequest('/auth/logout', { method: 'POST' });
    } catch {}
    window.location.href = '/index.html';
}

async function updateCartBadge() {
    const badge = document.getElementById('cart-badge');
    if (!badge) return;
    try {
        const cart = await apiRequest('/cart');
        badge.textContent = cart.itemCount || 0;
        badge.style.display = (cart.itemCount > 0) ? 'flex' : 'none';
    } catch {
        badge.style.display = 'none';
    }
}
