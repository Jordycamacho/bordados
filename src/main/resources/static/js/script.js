document.getElementById('menu-toggle').addEventListener('click', function() {
    var navLinks = document.getElementById('nav-links');
    navLinks.classList.toggle('active');
});


document.addEventListener('DOMContentLoaded', function () {
    // Segundo bordado
    const secondEmbroideryRadio = document.querySelectorAll('input[name="second-embroidery"]');
    const secondEmbroiderySection = document.getElementById('second-embroidery-section');

    secondEmbroideryRadio.forEach(radio => {
        radio.addEventListener('change', function () {
            if (this.value === 'yes') {
                secondEmbroiderySection.classList.remove('hidden');
            } else {
                secondEmbroiderySection.classList.add('hidden');
            }
        });
    });

    // Bordado de manga
    const sleeveEmbroideryRadio = document.querySelectorAll('input[name="sleeve-embroidery"]');
    const sleeveEmbroiderySection = document.getElementById('sleeve-embroidery-section');

    sleeveEmbroideryRadio.forEach(radio => {
        radio.addEventListener('change', function () {
            if (this.value === 'yes') {
                sleeveEmbroiderySection.classList.remove('hidden');
            } else {
                sleeveEmbroiderySection.classList.add('hidden');
            }
        });
    });
});



document.querySelector('.form').addEventListener('submit', function (event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const confirmEmail = document.getElementById('confirm-email').value;
    const password = document.getElementById('password').value;

    // Verificar que los correos coincidan
    if (email !== confirmEmail) {
        alert('Los correos electrónicos no coinciden.');
        return;
    }

    // Verificar que la contraseña tenga al menos 6 caracteres
    if (password.length < 6) {
        alert('La contraseña debe tener al menos 6 caracteres.');
        return;
    }

    // Si todo está bien, enviar el formulario
    alert('Registro exitoso. Verifica tu correo electrónico.');
    // Aquí podrías agregar lógica para enviar el formulario al servidor
});