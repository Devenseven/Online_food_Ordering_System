let editingId = null;

document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user || user.role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
    updateNav(user);
    loadCategories();
    loadFoods();
});

async function loadCategories() {
    try {
        const cats = await apiRequest('/admin/categories');
        const select = document.getElementById('foodCategory');
        select.innerHTML = '';
        cats.forEach(cat => {
            const opt = document.createElement('option');
            opt.value = cat.id;
            opt.textContent = cat.name;
            select.appendChild(opt);
        });
    } catch (e) {
        console.error(e);
    }
}

async function loadFoods() {
    try {
        const data = await apiRequest('/admin/foods?page=0&size=100');
        const tbody = document.getElementById('foodsTable');
        tbody.innerHTML = data.content.map(food => `
            <tr>
                <td>${food.id}</td>
                <td>${escapeHtml(food.name)}</td>
                <td>${escapeHtml(food.categoryName || '')}</td>
                <td>${formatPrice(food.price)}</td>
                <td>${food.available ? '✅' : '❌'}</td>
                <td>${food.active ? '✅' : '❌'}</td>
                <td style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                    <button onclick="editFood(${food.id})" class="btn" style="padding: 0.25rem 0.75rem;">Edit</button>
                    <button onclick="toggleAvailability(${food.id}, ${!food.available})" class="btn btn-secondary" style="padding: 0.25rem 0.75rem;">${food.available ? 'Disable' : 'Enable'}</button>
                    <button onclick="deleteFood(${food.id})" class="btn btn-danger" style="padding: 0.25rem 0.75rem;">Deactivate</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        showToast('Error loading foods', 'error');
    }
}

function openAddModal() {
    editingId = null;
    document.getElementById('modalTitle').textContent = 'Add Food';
    document.getElementById('foodId').value = '';
    document.getElementById('foodName').value = '';
    document.getElementById('foodDesc').value = '';
    document.getElementById('foodPrice').value = '';
    document.getElementById('foodImage').value = '';
    document.getElementById('foodAvailable').checked = true;
    document.getElementById('foodActive').checked = true;
    document.getElementById('foodModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('foodModal').style.display = 'none';
}

async function editFood(id) {
    try {
        const food = await apiRequest('/foods/' + id);
        editingId = id;
        document.getElementById('modalTitle').textContent = 'Edit Food';
        document.getElementById('foodName').value = food.name;
        document.getElementById('foodDesc').value = food.description || '';
        document.getElementById('foodPrice').value = food.price;
        document.getElementById('foodImage').value = food.imageUrl || '';
        document.getElementById('foodCategory').value = food.categoryId;
        document.getElementById('foodAvailable').checked = food.available;
        document.getElementById('foodActive').checked = food.active;
        document.getElementById('foodModal').style.display = 'flex';
    } catch (e) {
        showToast('Error loading food', 'error');
    }
}

document.getElementById('foodForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = {
        name: document.getElementById('foodName').value,
        description: document.getElementById('foodDesc').value,
        price: parseFloat(document.getElementById('foodPrice').value),
        imageUrl: document.getElementById('foodImage').value,
        categoryId: parseInt(document.getElementById('foodCategory').value),
        available: document.getElementById('foodAvailable').checked,
        active: document.getElementById('foodActive').checked
    };
    
    try {
        if (editingId) {
            await apiRequest('/admin/foods/' + editingId, { method: 'PUT', body: JSON.stringify(data) });
            showToast('Food updated');
        } else {
            await apiRequest('/admin/foods', { method: 'POST', body: JSON.stringify(data) });
            showToast('Food added');
        }
        closeModal();
        loadFoods();
    } catch (e) {
        showToast(e.message || 'Failed to save food', 'error');
    }
});

async function toggleAvailability(id, available) {
    try {
        await apiRequest(`/admin/foods/${id}/availability?available=${available}`, { method: 'PATCH' });
        showToast('Availability updated');
        loadFoods();
    } catch (e) {
        showToast(e.message || 'Failed to update', 'error');
    }
}

async function deleteFood(id) {
    if (!confirm('Deactivate this food?')) return;
    try {
        await apiRequest('/admin/foods/' + id, { method: 'DELETE' });
        showToast('Food deactivated');
        loadFoods();
    } catch (e) {
        showToast(e.message || 'Failed to deactivate', 'error');
    }
}
