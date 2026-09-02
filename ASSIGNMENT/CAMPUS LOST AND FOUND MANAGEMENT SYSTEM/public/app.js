document.addEventListener('DOMContentLoaded', () => {
  // DOM References
  const themeToggleBtn = document.getElementById('themeToggleBtn');
  const themeIcon = document.getElementById('themeIcon');
  const themeText = document.getElementById('themeText');
  
  const statTotal = document.getElementById('statTotal');
  const statLost = document.getElementById('statLost');
  const statFound = document.getElementById('statFound');
  const statReturned = document.getElementById('statReturned');
  const connectionModeBadge = document.getElementById('connectionModeBadge');

  const filterKeyword = document.getElementById('filterKeyword');
  const filterCategory = document.getElementById('filterCategory');
  const filterStatus = document.getElementById('filterStatus');
  const filterLocation = document.getElementById('filterLocation');
  const btnResetFilters = document.getElementById('btnResetFilters');

  const itemsTableBody = document.getElementById('itemsTableBody');
  const recordCountText = document.getElementById('recordCountText');

  const btnReportLostModal = document.getElementById('btnReportLostModal');
  const btnReportFoundModal = document.getElementById('btnReportFoundModal');
  const btnRefreshList = document.getElementById('btnRefreshList');

  // Modal References
  const itemModal = document.getElementById('itemModal');
  const itemForm = document.getElementById('itemForm');
  const modalTitle = document.getElementById('modalTitle');
  const modalCloseBtn = document.getElementById('modalCloseBtn');
  const modalCancelBtn = document.getElementById('modalCancelBtn');

  const modalItemId = document.getElementById('modalItemId');
  const modalItemName = document.getElementById('modalItemName');
  const modalCategory = document.getElementById('modalCategory');
  const modalStatus = document.getElementById('modalStatus');
  const modalDescription = document.getElementById('modalDescription');
  const modalLocation = document.getElementById('modalLocation');
  const modalDateReported = document.getElementById('modalDateReported');
  const modalReportedBy = document.getElementById('modalReportedBy');
  const modalContact = document.getElementById('modalContact');

  // Delete Modal References
  const deleteModal = document.getElementById('deleteModal');
  const deleteCloseBtn = document.getElementById('deleteCloseBtn');
  const deleteCancelBtn = document.getElementById('deleteCancelBtn');
  const deleteConfirmBtn = document.getElementById('deleteConfirmBtn');
  const deleteItemSummaryText = document.getElementById('deleteItemSummaryText');
  let itemToDeleteId = null;

  // Theme Management
  const currentTheme = localStorage.getItem('theme') || 'light';
  document.documentElement.setAttribute('data-theme', currentTheme);
  updateThemeUI(currentTheme);

  themeToggleBtn.addEventListener('click', () => {
    const nextTheme = document.documentElement.getAttribute('data-theme') === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', nextTheme);
    localStorage.setItem('theme', nextTheme);
    updateThemeUI(nextTheme);
  });

  function updateThemeUI(theme) {
    if (theme === 'dark') {
      themeIcon.textContent = '☀️';
      themeText.textContent = 'Light Mode';
    } else {
      themeIcon.textContent = '🌙';
      themeText.textContent = 'Dark Mode';
    }
  }

  // Initial Date Default (Today)
  const todayStr = new Date().toISOString().split('T')[0];
  modalDateReported.value = todayStr;

  // Load Data on Startup
  loadDashboardStats();
  loadItems();

  // Event Handlers for Filters
  let filterDebounceTimer;
  const triggerSearch = () => {
    clearTimeout(filterDebounceTimer);
    filterDebounceTimer = setTimeout(loadItems, 200);
  };

  filterKeyword.addEventListener('input', triggerSearch);
  filterLocation.addEventListener('input', triggerSearch);
  filterCategory.addEventListener('change', loadItems);
  filterStatus.addEventListener('change', loadItems);

  btnResetFilters.addEventListener('click', () => {
    filterKeyword.value = '';
    filterCategory.value = 'All Categories';
    filterStatus.value = 'All Statuses';
    filterLocation.value = '';
    loadItems();
  });

  btnRefreshList.addEventListener('click', () => {
    loadDashboardStats();
    loadItems();
    showToast('Refreshed data list and statistics!', 'success');
  });

  // Modal Open Buttons
  btnReportLostModal.addEventListener('click', () => {
    openItemModal('Report Lost Item', { status: 'Lost' });
  });

  btnReportFoundModal.addEventListener('click', () => {
    openItemModal('Report Found Item', { status: 'Found' });
  });

  modalCloseBtn.addEventListener('click', closeItemModal);
  modalCancelBtn.addEventListener('click', closeItemModal);

  deleteCloseBtn.addEventListener('click', closeDeleteModal);
  deleteCancelBtn.addEventListener('click', closeDeleteModal);

  // Form Submit Handler
  itemForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const payload = {
      item_name: modalItemName.value.trim(),
      category: modalCategory.value,
      status: modalStatus.value,
      description: modalDescription.value.trim(),
      location: modalLocation.value.trim(),
      date_reported: modalDateReported.value,
      reported_by: modalReportedBy.value.trim(),
      contact: modalContact.value.trim()
    };

    const editId = modalItemId.value;

    try {
      let res;
      if (editId) {
        // Update
        res = await fetch(`/api/items/${editId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      } else {
        // Create
        res = await fetch('/api/items', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      }

      if (!res.ok) {
        const data = await res.json();
        throw new Error(data.error || 'Failed to save item');
      }

      showToast(editId ? 'Item updated successfully!' : 'Item reported successfully!', 'success');
      closeItemModal();
      loadDashboardStats();
      loadItems();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  deleteConfirmBtn.addEventListener('click', async () => {
    if (!itemToDeleteId) return;

    try {
      const res = await fetch(`/api/items/${itemToDeleteId}`, {
        method: 'DELETE'
      });

      if (!res.ok) throw new Error('Failed to delete record');

      showToast('Item deleted successfully!', 'success');
      closeDeleteModal();
      loadDashboardStats();
      loadItems();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // API Call Functions
  async function loadDashboardStats() {
    try {
      const res = await fetch('/api/stats');
      if (!res.ok) return;
      const data = await res.json();

      statTotal.textContent = data.total || 0;
      statLost.textContent = data.lost || 0;
      statFound.textContent = data.found || 0;
      statReturned.textContent = data.returned || 0;

      if (data.mode === 'MySQL') {
        connectionModeBadge.textContent = 'Storage: MySQL Connected';
        connectionModeBadge.className = 'badge badge-returned';
      } else {
        connectionModeBadge.textContent = 'Storage: In-Memory (MySQL Offline)';
        connectionModeBadge.className = 'badge badge-found';
      }
    } catch (err) {
      console.error('Stats fetch error:', err);
    }
  }

  async function loadItems() {
    const params = new URLSearchParams();
    if (filterKeyword.value.trim()) params.append('keyword', filterKeyword.value.trim());
    if (filterCategory.value !== 'All Categories') params.append('category', filterCategory.value);
    if (filterStatus.value !== 'All Statuses') params.append('status', filterStatus.value);
    if (filterLocation.value.trim()) params.append('location', filterLocation.value.trim());

    try {
      const res = await fetch(`/api/items?${params.toString()}`);
      if (!res.ok) throw new Error('Failed to fetch items');
      const items = await res.json();

      renderItemsTable(items);
      recordCountText.textContent = `Showing ${items.length} item(s)`;
    } catch (err) {
      itemsTableBody.innerHTML = `<tr><td colspan="9" style="text-align:center; padding:20px; color:#dc2626;">Failed to load items. ${err.message}</td></tr>`;
    }
  }

  function renderItemsTable(items) {
    if (!items || items.length === 0) {
      itemsTableBody.innerHTML = `
        <tr>
          <td colspan="9" style="text-align:center; padding: 30px; color: var(--text-muted);">
            No matching lost or found records found.
          </td>
        </tr>
      `;
      return;
    }

    itemsTableBody.innerHTML = items.map(item => {
      let badgeClass = 'badge-lost';
      if (item.status === 'Found') badgeClass = 'badge-found';
      if (item.status === 'Returned') badgeClass = 'badge-returned';

      const dateFormatted = item.date_reported ? item.date_reported.split('T')[0] : 'N/A';

      return `
        <tr>
          <td><strong>#${item.item_id}</strong></td>
          <td>
            <div style="font-weight: 700;">${escapeHTML(item.item_name)}</div>
            <div style="font-size: 0.8rem; color: var(--text-muted); line-height: 1.3;">${escapeHTML(item.description)}</div>
          </td>
          <td>${escapeHTML(item.category)}</td>
          <td><span class="badge ${badgeClass}">${escapeHTML(item.status)}</span></td>
          <td>${escapeHTML(item.location)}</td>
          <td>${dateFormatted}</td>
          <td>${escapeHTML(item.reported_by)}</td>
          <td>${escapeHTML(item.contact)}</td>
          <td style="text-align: right;">
            <div style="display: flex; gap: 6px; justify-content: flex-end;">
              ${item.status !== 'Returned' ? `
                <button class="btn btn-secondary btn-sm" onclick="markAsReturned(${item.item_id}, '${escapeQuotes(item.item_name)}')">
                  ✅ Return
                </button>
              ` : ''}
              <button class="btn btn-secondary btn-sm" onclick="editItem(${item.item_id})">
                ✏️ Edit
              </button>
              <button class="btn btn-secondary btn-sm" style="color: #dc2626;" onclick="confirmDelete(${item.item_id}, '${escapeQuotes(item.item_name)}')">
                🗑️
              </button>
            </div>
          </td>
        </tr>
      `;
    }).join('');
  }

  // Global window functions for table button inline onclick calls
  window.editItem = async function(id) {
    try {
      const res = await fetch(`/api/items/${id}`);
      if (!res.ok) throw new Error('Item not found');
      const item = await res.json();
      openItemModal(`Edit Item #${item.item_id}`, item);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  window.markAsReturned = async function(id, name) {
    try {
      const res = await fetch(`/api/items/${id}/return`, { method: 'PUT' });
      if (!res.ok) throw new Error('Failed to update status');
      showToast(`Item #${id} ('${name}') marked as RETURNED!`, 'success');
      loadDashboardStats();
      loadItems();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  window.confirmDelete = function(id, name) {
    itemToDeleteId = id;
    deleteItemSummaryText.textContent = `ID #${id} - ${name}`;
    deleteModal.classList.add('active');
  };

  // Modal Utility Functions
  function openItemModal(title, data = {}) {
    modalTitle.textContent = title;
    modalItemId.value = data.item_id || '';
    modalItemName.value = data.item_name || '';
    modalCategory.value = data.category || 'Electronics';
    modalStatus.value = data.status || 'Lost';
    modalDescription.value = data.description || '';
    modalLocation.value = data.location || '';
    modalDateReported.value = data.date_reported ? data.date_reported.split('T')[0] : todayStr;
    modalReportedBy.value = data.reported_by || '';
    modalContact.value = data.contact || '';

    itemModal.classList.add('active');
  }

  function closeItemModal() {
    itemModal.classList.remove('active');
    itemForm.reset();
    modalDateReported.value = todayStr;
  }

  function closeDeleteModal() {
    deleteModal.classList.remove('active');
    itemToDeleteId = null;
  }

  // Helper Escape Functions
  function escapeHTML(str) {
    if (!str) return '';
    return str.replace(/[&<>'"]/g, 
      tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
  }

  function escapeQuotes(str) {
    if (!str) return '';
    return str.replace(/'/g, "\\'").replace(/"/g, '&quot;');
  }

  // Toast System
  function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.style.borderLeft = type === 'success' ? '4px solid #059669' : (type === 'error' ? '4px solid #dc2626' : '4px solid #2563eb');
    toast.innerHTML = `<span>${type === 'success' ? '✅' : (type === 'error' ? '❌' : 'ℹ️')}</span> <span>${escapeHTML(message)}</span>`;

    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  }
});
