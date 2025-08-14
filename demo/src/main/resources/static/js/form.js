document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('contactForm');

  const inputs = {
    nombres: document.getElementById('nombres'),
    apellidos: document.getElementById('apellidos'),
    correo: document.getElementById('correo'),
    semestre: document.getElementById('semestre'),
    descripcion: document.getElementById('descripcion'),
    charCounter: document.getElementById('char-counter')
  };

  const MAX_DESC = parseInt(inputs.descripcion.getAttribute('maxlength')) || 500;

  // Helper: crea/obtiene un <span class="error-text"> junto al input
  function getErrorSpan(el) {
    let span = el.parentNode.querySelector('.error-text');
    if (!span) {
      span = document.createElement('span');
      span.className = 'error-text';
      el.parentNode.appendChild(span);
    }
    return span;
  }

  // Normaliza: quita diacríticos y pasa a mayúsculas
  function normalizeAndUppercase(s) {
    if (!s) return '';
    // descomponer y quitar marcas diacríticas
    s = s.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    // también normaliza ß -> SS si aparece
    s = s.replace(/\u00DF/g, 'SS');
    return s.toUpperCase();
  }

  // Valida correo según el patrón requerido (todo en MAYÚSCULAS y sin espacios)
  const emailPattern = /^[A-Z0-9._%+\-]+@[A-Z0-9.\-]+\.[A-Z]{2,}$/;

  // Evita que el cursor salte al actualizar el value (intentar mantener caret)
  function setValuePreserveCaret(input, newValue) {
    try {
      const start = input.selectionStart;
      const end = input.selectionEnd;
      input.value = newValue;
      // ajustar caret si es válido
      const pos = Math.min(newValue.length, start);
      input.setSelectionRange(pos, pos);
    } catch (e) {
      input.value = newValue; // fallback
    }
  }

  // ---- EVENTOS para nombres y apellidos ----
  ['nombres', 'apellidos'].forEach(id => {
    const el = inputs[id];
    el.addEventListener('input', () => {
      let v = el.value;
      v = normalizeAndUppercase(v);
      if (el.maxLength > 0) v = v.slice(0, el.maxLength);
      setValuePreserveCaret(el, v);

      // validación simple: obligatorio y longitud
      const span = getErrorSpan(el);
      if (!v.trim()) {
        span.textContent = 'Este campo es obligatorio.';
        el.classList.add('invalid');
      } else {
        span.textContent = '';
        el.classList.remove('invalid');
      }
    });

    // Normalizar en pegar
    el.addEventListener('paste', (e) => {
      e.preventDefault();
      const text = (e.clipboardData || window.clipboardData).getData('text');
      const norm = normalizeAndUppercase(text);
      // insertar en caret position
      const start = el.selectionStart;
      const end = el.selectionEnd;
      const newVal = (el.value.slice(0, start) + norm + el.value.slice(end)).slice(0, el.maxLength);
      setValuePreserveCaret(el, newVal);
      el.dispatchEvent(new Event('input'));
    });
  });

  // ---- EVENTOS para correo ----
  inputs.correo.addEventListener('input', () => {
    let v = inputs.correo.value;
    // quitar espacios
    v = v.replace(/\s+/g, '');
    // normalizar tildes/diacríticos y mayúsculas
    v = normalizeAndUppercase(v);
    // limitar por maxlength si aplica
    if (inputs.correo.maxLength > 0) v = v.slice(0, inputs.correo.maxLength);
    setValuePreserveCaret(inputs.correo, v);

    const span = getErrorSpan(inputs.correo);

    // chequeos específicos
    if (!v) {
      span.textContent = 'El correo es obligatorio.';
      inputs.correo.classList.add('invalid');
      return;
    }

    // no permitir caracteres raros (solo el set del patrón)
    if (!emailPattern.test(v)) {
      span.textContent = 'Correo inválido. Debe tener "@" y al menos un punto después, sin espacios ni tildes, todo en MAYÚSCULAS.';
      inputs.correo.classList.add('invalid');
    } else {
      span.textContent = '';
      inputs.correo.classList.remove('invalid');
    }
  });

  inputs.correo.addEventListener('paste', (e) => {
    e.preventDefault();
    const text = (e.clipboardData || window.clipboardData).getData('text');
    let norm = text.replace(/\s+/g, '');
    norm = normalizeAndUppercase(norm);
    norm = norm.slice(0, inputs.correo.maxLength || 100);
    setValuePreserveCaret(inputs.correo, norm);
    inputs.correo.dispatchEvent(new Event('input'));
  });

  // ---- EVENTOS para semestre ----
  inputs.semestre.addEventListener('input', () => {
    const span = getErrorSpan(inputs.semestre);
    const v = inputs.semestre.value;
    if (v === '') {
      // opcional: si quieres obligatorio, descomentar
      // span.textContent = 'El semestre es obligatorio.';
      span.textContent = '';
      inputs.semestre.classList.remove('invalid');
      return;
    }
    const num = Number(v);
    if (!Number.isInteger(num) || num < 0 || num > 16) {
      span.textContent = 'Semestre inválido. Debe ser un número entero entre 0 y 16.';
      inputs.semestre.classList.add('invalid');
    } else {
      span.textContent = '';
      inputs.semestre.classList.remove('invalid');
    }
  });

  // ---- EVENTOS para descripción (contador y límite) ----
  function updateCharCounter() {
    const len = inputs.descripcion.value.length;
    inputs.charCounter.textContent = `${len}/${MAX_DESC} caracteres`;
  }

  // impedir más caracteres al alcanzar el máximo
  inputs.descripcion.addEventListener('input', () => {
    if (inputs.descripcion.value.length > MAX_DESC) {
      inputs.descripcion.value = inputs.descripcion.value.slice(0, MAX_DESC);
    }
    updateCharCounter();

    const span = getErrorSpan(inputs.descripcion);
    if (!inputs.descripcion.value.trim()) {
      span.textContent = 'La descripción es obligatoria.';
      inputs.descripcion.classList.add('invalid');
    } else {
      span.textContent = '';
      inputs.descripcion.classList.remove('invalid');
    }
  });

  // prevención en paste
  inputs.descripcion.addEventListener('paste', (e) => {
    e.preventDefault();
    const text = (e.clipboardData || window.clipboardData).getData('text');
    const allowed = text.slice(0, MAX_DESC - inputs.descripcion.value.length);
    const start = inputs.descripcion.selectionStart;
    const end = inputs.descripcion.selectionEnd;
    const newVal = inputs.descripcion.value.slice(0, start) + allowed + inputs.descripcion.value.slice(end);
    inputs.descripcion.value = newVal.slice(0, MAX_DESC);
    updateCharCounter();
    inputs.descripcion.dispatchEvent(new Event('input'));
  });

  // init contador
  updateCharCounter();

  // ---- VALIDACIÓN final en submit ----
  form.addEventListener('submit', (e) => {
    let firstInvalid = null;

    // Forzar disparo de validaciones en todos los campos
    ['nombres','apellidos','correo','semestre','descripcion'].forEach(id => {
      const el = inputs[id];
      el.dispatchEvent(new Event('input'));
    });

    // comprobar si hay elementos con clase invalid
    const invalids = form.querySelectorAll('.invalid');
    if (invalids.length > 0) {
      e.preventDefault();
      firstInvalid = invalids[0];
      firstInvalid.focus();
      // opcional: mostrar toast o mensaje global
      return false;
    }

    // adicional: última comprobación por si el pattern del correo falla
    const correoVal = inputs.correo.value || '';
    if (!emailPattern.test(correoVal)) {
      e.preventDefault();
      const span = getErrorSpan(inputs.correo);
      span.textContent = 'Correo inválido. Revisa formato.';
      inputs.correo.classList.add('invalid');
      inputs.correo.focus();
      return false;
    }

    return true;
  });

}); 