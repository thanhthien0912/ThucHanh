/**
 * Main JavaScript - Hệ thống Quản lý Nhà sách
 * Author: Phan Thanh Thien - MSSV: 2280603036
 * Course: CMP3025 - J2EE
 *
 * Modern JavaScript with Tailwind CSS support
 */

// ============================================
// DOM READY
// ============================================
document.addEventListener('DOMContentLoaded', function () {
    console.log('🚀 Bookstore Management System initialized');

    // Initialize all components
    initScrollAnimations();
    initCardAnimations();
    initNavbarEffects();
    initFormEnhancements();
    initCounterAnimations();
    initRippleEffect();
    initAutoDismissAlerts();
});

// ============================================
// SCROLL ANIMATIONS (Intersection Observer)
// ============================================
function initScrollAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-fadeIn');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Observe elements with animate-on-scroll class
    const animateElements = document.querySelectorAll('.animate-on-scroll, .card, .feature-card');
    animateElements.forEach(el => {
        el.style.opacity = '0';
        observer.observe(el);
    });
}

// ============================================
// CARD HOVER ANIMATIONS
// ============================================
function initCardAnimations() {
    const cards = document.querySelectorAll('.card, .feature-card');

    cards.forEach(card => {
        if (!card.classList.contains('card-hover')) return;

        card.addEventListener('mousemove', function (e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            // Subtle tilt effect
            const centerX = rect.width / 2;
            const centerY = rect.height / 2;
            const tiltX = (y - centerY) / 20;
            const tiltY = (centerX - x) / 20;

            this.style.transform = `perspective(1000px) rotateX(${tiltX}deg) rotateY(${tiltY}deg) translateY(-4px)`;
        });

        card.addEventListener('mouseleave', function () {
            this.style.transform = '';
        });
    });
}

// ============================================
// NAVBAR EFFECTS
// ============================================
function initNavbarEffects() {
    const navbar = document.querySelector('nav.sticky-top, nav.sticky');
    if (!navbar) return;

    let lastScroll = 0;

    window.addEventListener('scroll', () => {
        const currentScroll = window.pageYOffset;

        // Add shadow on scroll
        if (currentScroll > 10) {
            navbar.classList.add('shadow-md');
        } else {
            navbar.classList.remove('shadow-md');
        }

        // Hide/show navbar on scroll
        if (currentScroll > lastScroll && currentScroll > 100) {
            navbar.style.transform = 'translateY(-100%)';
        } else {
            navbar.style.transform = 'translateY(0)';
        }

        lastScroll = currentScroll;
    });

    navbar.style.transition = 'transform 0.3s ease, box-shadow 0.3s ease';
}

// ============================================
// FORM ENHANCEMENTS
// ============================================
function initFormEnhancements() {
    // Focus animations
    const inputs = document.querySelectorAll('input, textarea, select');

    inputs.forEach(input => {
        input.addEventListener('focus', function () {
            this.parentElement.classList.add('input-focused');
        });

        input.addEventListener('blur', function () {
            this.parentElement.classList.remove('input-focused');
        });
    });

    // Submit button loading state
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function (e) {
            const submitBtn = this.querySelector('button[type="submit"], input[type="submit"]');
            if (submitBtn && !submitBtn.classList.contains('no-loading')) {
                const originalText = submitBtn.innerHTML;
                submitBtn.innerHTML = '<span class="spinner mr-2"></span>Đang xử lý...';
                submitBtn.disabled = true;

                // Add spinner animation inline
                const style = document.createElement('style');
                style.textContent = `
                    .spinner {
                        display: inline-block;
                        width: 16px;
                        height: 16px;
                        border: 2px solid rgba(255, 255, 255, 0.3);
                        border-top-color: currentColor;
                        border-radius: 50%;
                        animation: spin 0.8s linear infinite;
                    }
                    @keyframes spin {
                        to { transform: rotate(360deg); }
                    }
                `;
                document.head.appendChild(style);

                // Re-enable after 10 seconds (failsafe)
                setTimeout(() => {
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                }, 10000);
            }
        });
    });
}

// ============================================
// COUNTER ANIMATIONS
// ============================================
function initCounterAnimations() {
    const counters = document.querySelectorAll('.display-6, .text-3xl, .text-4xl');

    counters.forEach(counter => {
        const text = counter.textContent;
        const value = parseInt(text);

        if (!isNaN(value) && value > 0) {
            animateCounter(counter, 0, value, 1500);
        }
    });
}

function animateCounter(element, start, end, duration) {
    const range = end - start;
    const startTime = performance.now();

    function updateCounter(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);

        // Easing function (ease-out)
        const easeOut = 1 - Math.pow(1 - progress, 3);
        const current = Math.floor(start + range * easeOut);

        element.textContent = current.toLocaleString('vi-VN');

        if (progress < 1) {
            requestAnimationFrame(updateCounter);
        }
    }

    requestAnimationFrame(updateCounter);
}

// ============================================
// RIPPLE EFFECT FOR BUTTONS
// ============================================
function initRippleEffect() {
    const buttons = document.querySelectorAll('.btn-glow');

    buttons.forEach(button => {
        button.addEventListener('click', function (e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const ripple = document.createElement('span');
            ripple.style.cssText = `
                position: absolute;
                top: 50%;
                left: 50%;
                width: 0;
                height: 0;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.3);
                transform: translate(-50%, -50%);
                transition: width 0.6s, height 0.6s;
                pointer-events: none;
            `;

            this.appendChild(ripple);

            requestAnimationFrame(() => {
                ripple.style.width = '300px';
                ripple.style.height = '300px';
                ripple.style.opacity = '0';
            });

            setTimeout(() => ripple.remove(), 600);
        });
    });
}

// ============================================
// AUTO-DISMISS ALERTS
// ============================================
function initAutoDismissAlerts(timeout = 5000) {
    const alerts = document.querySelectorAll('.animate-fadeIn');
    alerts.forEach(function (alert, index) {
        if (alert.textContent.trim()) {
            setTimeout(function () {
                if (alert && alert.style) {
                    alert.style.transition = 'all 0.5s ease';
                    alert.style.opacity = '0';
                    alert.style.transform = 'translateX(100px)';

                    setTimeout(() => {
                        alert.remove();
                    }, 500);
                }
            }, timeout + (index * 500));
        }
    });
}

// ============================================
// UTILITY FUNCTIONS
// ============================================

/**
 * Confirm delete action
 */
function confirmDelete(itemName) {
    return confirm(`⚠️ Bạn có chắc chắn muốn xóa "${itemName}"?\n\nHành động này không thể hoàn tác!`);
}

/**
 * Format currency to VND
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

/**
 * Show toast notification (custom implementation for Tailwind)
 */
function showToast(message, type = 'info') {
    // Remove existing toasts if too many
    const existingToasts = document.querySelectorAll('.toast');
    if (existingToasts.length > 5) {
        existingToasts[0].remove();
    }

    const toast = document.createElement('div');
    const typeClasses = {
        success: 'toast-success',
        error: 'toast-error',
        info: 'toast-info',
        warning: 'toast-warning'
    };

    const typeIcons = {
        success: 'bi-check-circle',
        error: 'bi-exclamation-circle',
        info: 'bi-info-circle',
        warning: 'bi-exclamation-triangle'
    };

    toast.className = `toast ${typeClasses[type] || 'toast-info'}`;
    toast.innerHTML = `
        <div class="toast-icon">
            <i class="bi ${typeIcons[type] || typeIcons.info}"></i>
        </div>
        <div class="toast-message">${message}</div>
        <button class="toast-close" onclick="this.parentElement.remove()">
            <i class="bi bi-x-lg"></i>
        </button>
    `;

    // Add toast container if not exists
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }

    toastContainer.appendChild(toast);

    // Auto dismiss
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100px)';
        setTimeout(() => toast.remove(), 300);
    }, 5000);
}

/**
 * Copy text to clipboard
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showToast('Đã sao chép vào clipboard!', 'success');
    } catch (err) {
        showToast('Không thể sao chép!', 'error');
    }
}

// ============================================
// KEYBOARD SHORTCUTS
// ============================================
document.addEventListener('keydown', function (e) {
    // Ctrl + K: Focus search
    if (e.ctrlKey && e.key === 'k') {
        e.preventDefault();
        const searchInput = document.querySelector('input[type="search"], input[name="q"]');
        if (searchInput) searchInput.focus();
    }

    // Escape: Close mobile menu
    if (e.key === 'Escape') {
        const mobileMenu = document.getElementById('mobileMenu');
        if (mobileMenu && !mobileMenu.classList.contains('hidden')) {
            mobileMenu.classList.add('hidden');
            const menuIcon = document.getElementById('menuIcon');
            if (menuIcon) {
                menuIcon.classList.remove('bi-x-lg');
                menuIcon.classList.add('bi-list');
            }
        }
    }
});

// ============================================
// DARK MODE SUPPORT (Future)
// ============================================
function toggleDarkMode() {
    document.body.classList.toggle('dark');
    localStorage.setItem('darkMode', document.body.classList.contains('dark'));
}

// Check saved preference
if (localStorage.getItem('darkMode') === 'true') {
    document.body.classList.add('dark');
}

console.log('✅ All JavaScript modules loaded successfully!');
