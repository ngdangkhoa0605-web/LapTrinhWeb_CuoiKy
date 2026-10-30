document.addEventListener('DOMContentLoaded', function () {
	// Give each submitted form immediate, accessible feedback without replacing its submit button.
	document.querySelectorAll('form').forEach(function (form) {
		form.addEventListener('submit', function () {
			if (typeof form.checkValidity === 'function' && !form.checkValidity()) {
				form.classList.add('was-validated');
				return;
			}
			if (form.dataset.submitting === 'true') return;
			var button = form.querySelector('button[type="submit"], input[type="submit"]');
			if (!button || button.disabled) return;
			form.dataset.submitting = 'true';
			button.dataset.originalText = button.tagName === 'INPUT' ? button.value : button.innerHTML;
			if (button.tagName === 'INPUT') {
				button.value = 'Đang xử lý…';
			} else {
				button.innerHTML = '<span class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>' + button.dataset.originalText;
				button.setAttribute('aria-live', 'polite');
			}
			button.disabled = true;
		});
	});
});
