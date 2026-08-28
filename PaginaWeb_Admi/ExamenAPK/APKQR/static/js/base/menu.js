document.addEventListener("DOMContentLoaded", () => {

    const currentPath = window.location.pathname;

    document.querySelectorAll(".menu a").forEach(link => {

        if (link.getAttribute("href") === currentPath) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }

    });

});