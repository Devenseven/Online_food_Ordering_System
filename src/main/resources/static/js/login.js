document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    updateNav(user);
    updateCartBadge();
    
    const form = document.getElementById('loginForm');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        document.getElementById('emailError').textContent = '';
        document.getElementById('passwordError').textContent = '';
        document.getElementById('generalError').textContent = '';
        
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        
        try {
            await apiRequest('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email, password })
            });
            showToast('Login successful!');
            const params = new URLSearchParams(window.location.search);
            const redirect = params.get('redirect') || '/menu.html';
            setTimeout(() => {
                window.location.href = redirect;
            }, 500);
        } catch (error) {
            document.getElementById('generalError').textContent = error.message || 'Invalid email or password';
        }
    });
});
