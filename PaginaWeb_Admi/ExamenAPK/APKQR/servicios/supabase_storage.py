import uuid
import requests
from .imagenes import comprimir_foto_carnet


SUPABASE_URL = "https://vtryizusiddlydztsgax.supabase.co"
SUPABASE_KEY = "sb_publishable_QYlxUuYrIu5keLsueqjLBg_O34SKhh-"
BUCKET_FOTOS = "fotos-estudiantes"


def _subir_foto(foto, identificador, carpeta):
    foto_bytes = comprimir_foto_carnet(foto)
    nombre_archivo = f"{carpeta}/{identificador}_{uuid.uuid4().hex}.jpg"
    url_subida = f"{SUPABASE_URL}/storage/v1/object/{BUCKET_FOTOS}/{nombre_archivo}"

    headers = {
        "Authorization": f"Bearer {SUPABASE_KEY}",
        "apikey": SUPABASE_KEY,
        "Content-Type": "image/jpeg",
        "x-upsert": "false"
    }

    respuesta = requests.post(url_subida, headers=headers, data=foto_bytes)

    if respuesta.status_code not in [200, 201]:
        raise Exception(f"Error al subir foto a Supabase Storage: {respuesta.text}")

    return f"{SUPABASE_URL}/storage/v1/object/public/{BUCKET_FOTOS}/{nombre_archivo}"


def _eliminar_foto(foto_url):
    if not foto_url:
        return

    marcador = f"/storage/v1/object/public/{BUCKET_FOTOS}/"

    if marcador not in foto_url:
        return

    ruta_archivo = foto_url.split(marcador)[1]
    url_eliminar = f"{SUPABASE_URL}/storage/v1/object/{BUCKET_FOTOS}/{ruta_archivo}"

    headers = {
        "Authorization": f"Bearer {SUPABASE_KEY}",
        "apikey": SUPABASE_KEY
    }

    respuesta = requests.delete(url_eliminar, headers=headers)

    if respuesta.status_code not in [200, 204, 404]:
        raise Exception(f"Error al eliminar foto de Storage: {respuesta.text}")


# =========================================================
# ESTUDIANTES
# =========================================================

def subir_foto_estudiante(foto, carnet):
    return _subir_foto(foto, carnet, "estudiantes")


def eliminar_foto_estudiante(foto_url):
    return _eliminar_foto(foto_url)


# =========================================================
# GUARDAS DE SEGURIDAD
# =========================================================

def subir_foto_guarda(foto, codigo):
    return _subir_foto(foto, codigo, "guardas")


def eliminar_foto_guarda(foto_url):
    return _eliminar_foto(foto_url)