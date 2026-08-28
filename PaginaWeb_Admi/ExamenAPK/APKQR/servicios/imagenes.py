from io import BytesIO
from PIL import Image


def comprimir_foto_carnet(foto, ancho=300, alto=300, calidad=75):
    """
    Recibe una foto subida desde Django.
    La recorta centrada, la convierte a tamaño carnet 300x300,
    la comprime como JPEG y devuelve la imagen en bytes.
    """

    imagen = Image.open(foto)

    # Convertir a RGB para evitar errores con PNG/transparencias
    imagen = imagen.convert("RGB")

    ancho_original, alto_original = imagen.size

    # Recorte centrado cuadrado
    if ancho_original > alto_original:
        diferencia = ancho_original - alto_original
        izquierda = diferencia // 2
        derecha = izquierda + alto_original
        imagen = imagen.crop((izquierda, 0, derecha, alto_original))

    elif alto_original > ancho_original:
        diferencia = alto_original - ancho_original
        arriba = diferencia // 2
        abajo = arriba + ancho_original
        imagen = imagen.crop((0, arriba, ancho_original, abajo))

    # Redimensionar a tamaño carnet
    imagen = imagen.resize((ancho, alto))

    buffer = BytesIO()

    imagen.save(
        buffer,
        format="JPEG",
        quality=calidad,
        optimize=True
    )

    foto_bytes = buffer.getvalue()

    return foto_bytes