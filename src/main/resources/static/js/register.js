document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    updateNav(user);
    updateCartBadge();
    
    const form = document.getElementById('registerForm');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        document.getElementById('fullNameError').textContent = '';
        document.getElementById('emailError').textContent = '';
        document.getElementById('passwordError').textContent = '';
        document.getElementById('confirmPasswordError').textContent = '';
        document.getElementById('generalError').textContent = '';
        
        const fullName = document.getElementById('fullName').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (password !== confirmPassword) {
            document.getElementById('confirmPasswordError').textContent = 'Passwords do not match';
            return;
        }
        
        try {
            await apiRequest('/auth/register', {
                method: 'POST',
                body: JSON.stringify({ fullName, email, password })
            });
            showToast('Registration successful! Please login.');
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 1000);
        } catch (error) {
            if (error.data && error.data.fieldErrors) {
                error.data.fieldErrors.forEach(fe => {
                    const el = document.getElementById(fe.field + 'Error');
                    if (el) el.textContent = fe.message;
                });
            } else {
                document.getElementById('generalError').textContent = error.message || 'Registration failed';
            }
        }
    });
});
