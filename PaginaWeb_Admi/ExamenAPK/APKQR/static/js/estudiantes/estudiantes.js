let currentMode = "create";

document.addEventListener("DOMContentLoaded", function () {
    const btnOpenCreate = document.getElementById("btn-open-create");
    const formEstudiante = document.getElementById("form-estudiante");
    const csvInput = document.getElementById("csv-file-input");
    const btnUploadCsv = document.getElementById("btn-upload-csv");
    const btnConfirmDelete = document.getElementById("btn-confirm-delete");

    if (btnOpenCreate) {
        btnOpenCreate.addEventListener("click", function () {
            openModal("create");
        });
    }

    if (formEstudiante) {
        formEstudiante.addEventListener("submit", saveEstudiante);
    }

    if (csvInput) {
        csvInput.addEventListener("change", handleCSVSelect);
    }

    if (btnUploadCsv) {
        btnUploadCsv.addEventListener("click", processCSV);
    }

    if (btnConfirmDelete) {
        btnConfirmDelete.addEventListener("click", executeDelete);
    }

    document.addEventListener("click", function (e) {
        const btnEditar = e.target.closest(".btn-editar");
        const btnEliminar = e.target.closest(".btn-eliminar");
        const btnCerrar = e.target.closest("[data-close-modal]");

        if (btnEditar) {
            abrirModalEditar(btnEditar);
        }

        if (btnEliminar) {
            abrirModalEliminar(btnEliminar);
        }

        if (btnCerrar) {
            const modalType = btnCerrar.dataset.closeModal;
            closeModal(modalType);
        }
    });
});

function getCookie(name) {
    let cookieValue = null;

    if (document.cookie && document.cookie !== "") {
        const cookies = document.cookie.split(";");

        for (let cookie of cookies) {
            cookie = cookie.trim();

            if (cookie.startsWith(name + "=")) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }

    return cookieValue;
}

function closeModal(modalType) {
    const modal = document.getElementById("modal-" + modalType);

    if (modal) {
        modal.classList.add("hidden");
    }
}

function openModal(type, data = null) {
    const form = document.getElementById("form-estudiante");
    const modalForm = document.getElementById("modal-form");

    if (!form || !modalForm) {
        return;
    }

    form.reset();

    if (type === "create") {
        currentMode = "create";

        document.getElementById("modal-title").innerText = "Registrar Estudiante";
        document.getElementById("form-carnet").disabled = false;

        modalForm.classList.remove("hidden");
    }

    if (type === "update") {
        currentMode = "update";

        document.getElementById("modal-title").innerText = "Actualizar Estudiante";

        document.getElementById("form-carnet").value = data.carnet;
        document.getElementById("form-carnet").disabled = true;

        document.getElementById("form-nombres").value = data.nombres;
        document.getElementById("form-apellidos").value = data.apellidos;
        document.getElementById("form-carrera").value = data.carrera;
        document.getElementById("form-edad").value = data.edad;
        document.getElementById("form-ano").value = data.ano;
        document.getElementById("form-procedencia").value = data.procedencia;

        modalForm.classList.remove("hidden");
    }

    if (type === "delete") {
        document.getElementById("delete-target-carnet").value = data;
        document.getElementById("modal-delete").classList.remove("hidden");
    }
}

function abrirModalEditar(btn) {
    const row = btn.closest("tr");

    if (!row) {
        return;
    }

    openModal("update", {
        carnet: row.dataset.carnet,
        nombres: row.dataset.nombres,
        apellidos: row.dataset.apellidos,
        carrera: row.dataset.carrera,
        edad: row.dataset.edad,
        ano: row.dataset.ano,
        procedencia: row.dataset.procedencia
    });
}

function abrirModalEliminar(btn) {
    const row = btn.closest("tr");

    if (!row) {
        return;
    }

    const carnet = row.dataset.carnet;

    openModal("delete", carnet);
}

function saveEstudiante(e) {
    e.preventDefault();

    const data = new FormData();

    data.append("carnet", document.getElementById("form-carnet").value);
    data.append("nombres", document.getElementById("form-nombres").value);
    data.append("apellidos", document.getElementById("form-apellidos").value);
    data.append("carrera", document.getElementById("form-carrera").value);
    data.append("edad", document.getElementById("form-edad").value);
    data.append("ano", document.getElementById("form-ano").value);
    data.append("procedencia", document.getElementById("form-procedencia").value);

    const fotoInput = document.getElementById("form-foto");

    if (fotoInput.files.length > 0) {
        data.append("foto", fotoInput.files[0]);
    }

    let url = "/crear-estudiante/";

    if (currentMode === "update") {
        url = "/actualizar-estudiante/";
    }

    fetch(url, {
        method: "POST",
        headers: {
            "X-CSRFToken": getCookie("csrftoken")
        },
        body: data
    })
    .then(res => res.json())
    .then(res => {
        if (res.mensaje === "ok") {
            location.reload();
        } else {
            alert("Error al guardar: " + res.detalle);
        }
    })
    .catch(error => {
        alert("Error de conexión.");
        console.log(error);
    });
}

function executeDelete() {
    const carnet = document.getElementById("delete-target-carnet").value;

    const data = new FormData();
    data.append("carnet", carnet);

    fetch("/eliminar-estudiante/", {
        method: "POST",
        headers: {
            "X-CSRFToken": getCookie("csrftoken")
        },
        body: data
    })
    .then(res => res.json())
    .then(res => {
        if (res.mensaje === "ok") {
            const row = document.getElementById(`row-${carnet}`);

            if (row) {
                row.remove();
            }

            closeModal("delete");

            alert("Estudiante eliminado correctamente.");
        } else {
            alert("Error al eliminar: " + res.detalle);
        }
    })
    .catch(error => {
        alert("Error de conexión al eliminar.");
        console.log(error);
    });
}

function handleCSVSelect() {
    const btn = document.getElementById("btn-upload-csv");

    btn.disabled = false;
    btn.classList.remove("disabled");
}

function processCSV() {
    alert("La carga CSV todavía no está conectada a la base de datos.");
}