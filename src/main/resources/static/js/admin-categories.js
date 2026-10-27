let editingId = null;

document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkAuth();
    if (!user || user.role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
    updateNav(user);
    loadCategories();
});

async function loadCategories() {
    try {
        const categories = await apiRequest('/admin/categories');
        const tbody = document.getElementById('categoriesTable');
        tbody.innerHTML = categories.map(cat => `
            <tr>
                <td>${cat.id}</td>
                <td>${escapeHtml(cat.name)}</td>
                <td>${escapeHtml(cat.description || '')}</td>
                <td>${cat.active ? '✅' : '❌'}</td>
                <td style="display: flex; gap: 0.5rem;">
                    <button onclick="editCategory(${cat.id})" class="btn" style="padding: 0.25rem 0.75rem;">Edit</button>
                    <button onclick="deleteCategory(${cat.id})" class="btn btn-danger" style="padding: 0.25rem 0.75rem;">Deactivate</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        showToast('Error loading categories', 'error');
    }
}

function openAddModal() {
    editingId = null;
    document.getElementById('modalTitle').textContent = 'Add Category';
    document.getElementById('categoryName').value = '';
    document.getElementById('categoryDesc').value = '';
    document.getElementById('categoryActive').checked = true;
    document.getElementById('categoryModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('categoryModal').style.display = 'none';
}

function editCategory(id) {
    // Find category in current data - simplified
    editingId = id;
    document.getElementById('modalTitle').textContent = 'Edit Category';
    document.getElementById('categoryModal').style.display = 'flex';
}

document.getElementById('categoryForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = {
        name: document.getElementById('categoryName').value,
        description: document.getElementById('categoryDesc').value,
        active: document.getElementById('categoryActive').checked
    };
    
    try {
        if (editingId) {
            await apiRequest('/admin/categories/' + editingId, { method: 'PUT', body: JSON.stringify(data) });
            showToast('Category updated');
        } else {
            await apiRequest('/admin/categories', { method: 'POST', body: JSON.stringify(data) });
            showToast('Category added');
        }
        closeModal();
        loadCategories();
    } catch (e) {
        showToast(e.message || 'Failed to save category', 'error');
    }
});

async function deleteCategory(id) {
    if (!confirm('Deactivate this category?')) return;
    try {
        await apiRequest('/admin/categories/' + id, { method: 'DELETE' });
        showToast('Category deactivated');
        loadCategories();
    } catch (e) {
        showToast(e.message || 'Failed to deactivate', 'error');
    }
}
