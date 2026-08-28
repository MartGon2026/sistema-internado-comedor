# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Asistencias(models.Model):
    id = models.BigAutoField(primary_key=True)
    carnet = models.ForeignKey('Estudiantes', models.DO_NOTHING, db_column='carnet')
    tipo_comida = models.TextField()
    codigo_utilizado = models.TextField()
    hora = models.TimeField()
    fecha = models.DateField()
    estado = models.BooleanField()

    class Meta:
        managed = False
        db_table = 'asistencias'
        unique_together = (('carnet', 'fecha', 'tipo_comida'),)


class Codigoqrcomedor(models.Model):
    id = models.BigAutoField(primary_key=True)
    codqr = models.TextField(db_column='CodQR')  # Field name made lowercase.
    fechacreacion = models.DateField(db_column='fechaCreacion', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'codigoQrComedor'
        db_table_comment = 'codigo que estarß de manera estatico'


class CodigosQr(models.Model):
    id = models.BigAutoField(primary_key=True)
    carnet = models.ForeignKey('Estudiantes', models.DO_NOTHING, db_column='carnet')
    codigo = models.TextField()
    tipo_comida = models.TextField()
    utilizado = models.BooleanField()
    fecha = models.DateField()
    hora = models.TimeField()

    class Meta:
        managed = False
        db_table = 'codigos_qr'


class Estudiantes(models.Model):
    carnet = models.TextField(primary_key=True)
    nombres = models.TextField()
    apellidos = models.TextField()
    carrera = models.TextField()
    edad_estudi = models.SmallIntegerField()
    procedencia = models.TextField(blank=True, null=True)
    telefono = models.TextField(blank=True, null=True)
    anocarrera = models.TextField(db_column='anoCarrera')  # Field name made lowercase.
    foto_estudi = models.TextField(blank=True, null=True)
    estado_internado = models.TextField()

    class Meta:
        managed = False
        db_table = 'estudiantes'


class HistorialInternado(models.Model):
    id = models.BigAutoField(primary_key=True)
    carnet = models.ForeignKey(Estudiantes, models.DO_NOTHING, db_column='carnet')
    fecha_salida = models.DateField()
    hora_salida = models.TimeField()
    fecha_entrada = models.DateField(blank=True, null=True)
    hora_entrada = models.TimeField(blank=True, null=True)
    estado = models.TextField(db_column='Estado', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'historial_internado'


class Usuarios(models.Model):
    id = models.BigAutoField(primary_key=True)
    carnet = models.OneToOneField(Estudiantes, models.DO_NOTHING, db_column='carnet', blank=True, null=True)
    usuario = models.TextField(unique=True)
    contrasena = models.TextField()
    rol = models.TextField()
    estado = models.BooleanField()
    sesion_activa = models.BooleanField()
    fotouser = models.TextField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'usuarios'

class GuardaSeguridad(models.Model):
    codigo_unico = models.TextField(db_column='codigoUnico', primary_key=True)
    nombre = models.TextField(db_column='Nombre')
    apellido = models.TextField(db_column='Apellido', blank=True, null=True)
    sexo = models.TextField(db_column='Sexo', blank=True, null=True)
    foto = models.TextField(db_column='Foto', blank=True, null=True)
    turno = models.TextField(db_column='Turno', blank=True, null=True)
    usuario = models.TextField(db_column='Usuario', blank=True, null=True)
    contrasena = models.TextField(db_column='Contrasena', blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'GuardaSeguridad'
