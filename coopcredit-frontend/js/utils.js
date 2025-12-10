// Utils - Helper functions

// Toast notifications
class ToastManager {
    constructor() {
        this.container = document.querySelector('.toast-container') || this.createContainer();
    }

    createContainer() {
        const container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
        return container;
    }

    show(message, type = 'info', duration = 4000) {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        this.container.appendChild(toast);

        setTimeout(() => {
            toast.style.animation = 'slideOutRight 0.3s ease forwards';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    }

    success(message, duration = 4000) {
        this.show(message, 'success', duration);
    }

    error(message, duration = 4000) {
        this.show(message, 'error', duration);
    }

    info(message, duration = 4000) {
        this.show(message, 'info', duration);
    }
}

const toast = new ToastManager();

// Date formatting
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('es-ES', options);
}

function formatDateTime(dateString) {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('es-ES', options);
}

// Currency formatting
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'COP'
    }).format(amount);
}

// Email validation
function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

// Password validation (minimum 6 characters)
function isValidPassword(password) {
    return password.length >= 6;
}

// Clear form
function clearForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.reset();
        form.querySelectorAll('.error').forEach(el => el.classList.remove('error'));
        form.querySelectorAll('.error-message').forEach(el => el.remove());
    }
}

// Show form errors
function showFormErrors(formId, errors) {
    clearForm(formId);
    const form = document.getElementById(formId);
    if (!form) return;

    Object.keys(errors).forEach(fieldName => {
        const input = form.querySelector(`[name="${fieldName}"]`);
        if (input) {
            input.classList.add('border-red-500');
            const errorMsg = document.createElement('p');
            errorMsg.className = 'text-red-500 text-sm mt-1';
            errorMsg.textContent = errors[fieldName];
            input.parentElement.appendChild(errorMsg);
        }
    });
}

// Show/hide modal
function showModal(modalId) {
    const overlay = document.getElementById(modalId);
    if (overlay) {
        overlay.classList.add('active');
    }
}

function hideModal(modalId) {
    const overlay = document.getElementById(modalId);
    if (overlay) {
        overlay.classList.remove('active');
    }
}

// Close modal when clicking outside
function setupModalClose(modalId) {
    const overlay = document.getElementById(modalId);
    if (overlay) {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                hideModal(modalId);
            }
        });
    }
}

// Loading spinner
function showLoading(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="flex justify-center items-center py-8">
                <div class="spinner"></div>
            </div>
        `;
    }
}

// Skeleton loading
function showSkeleton(containerId, rows = 5) {
    const container = document.getElementById(containerId);
    if (container) {
        let html = '';
        for (let i = 0; i < rows; i++) {
            html += `
                <div class="skeleton h-12 mb-2" style="border-radius: 4px;"></div>
            `;
        }
        container.innerHTML = html;
    }
}

// Empty state
function showEmptyState(containerId, message = 'No data available') {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
                <p>${message}</p>
            </div>
        `;
    }
}

// Debounce function
function debounce(func, delay) {
    let timeoutId;
    return function (...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func(...args), delay);
    };
}

// API error handling
function handleAPIError(error) {
    console.error('API Error:', error);

    if (error.status === 401) {
        toast.error('Session expired');
        auth.logout();
        return;
    }

    if (error.status === 400 || error.status === 422) {
        const data = error.data;

        // Validation error handling (structure: {type, title, status, detail, errors})
        if (data.errors && typeof data.errors === 'object') {
            // Extract all error messages
            const errorMessages = [];
            Object.keys(data.errors).forEach(field => {
                const fieldErrors = data.errors[field];
                if (Array.isArray(fieldErrors)) {
                    errorMessages.push(...fieldErrors);
                } else {
                    errorMessages.push(fieldErrors);
                }
            });

            if (errorMessages.length > 0) {
                toast.error(errorMessages.join(', '));
                return;
            }
        }

        // Show error detail or title
        if (data.detail) {
            toast.error(data.detail);
        } else if (data.title) {
            toast.error(data.title);
        } else if (data.message) {
            toast.error(data.message);
        } else {
            toast.error('Request error');
        }
        return;
    }

    if (error.status === 404) {
        toast.error('Resource not found');
        return;
    }

    if (error.status >= 500) {
        toast.error('Server error. Try again later.');
        return;
    }

    toast.error(error.message || 'Unknown error');
}

// Confirmation
function confirmAction(message = 'Are you sure?') {
    return confirm(message);
}
