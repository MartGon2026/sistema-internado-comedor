from django.urls import path
from . import views

urlpatterns = [

    #path('', views.testing, name='testing'),
    path('', views.index, name='index'),
    # Cargamos 'dashboard', NO 'base'
    path("dashboard/", views.dashboard, name="dashboard"),
    path('estudiantes/', views.estudiantes, name='estudiantes'),
    path('usuarios/', views.usuarios, name='usuarios'),
    path('crear-estudiante/', views.crear_estudiante, name='crear_estudiante'),
    path('eliminar-estudiante/', views.eliminar_estudiante, name='eliminar_estudiante'),
    path('actualizar-estudiante/', views.actualizar_estudiante, name='actualizar_estudiante'),
    path('eliminar-usuario/', views.eliminar_usuario, name='eliminar_usuario'),

     path('importar-excel/', views.importar_excel, name='importar_excel'),

    path('crear-usuario/', views.crear_usuario, name='crear_usuario'),   
    path('actualizar-usuario/', views.actualizar_usuario, name='actualizar_usuario'),
    path('actualizar-password-usuario/', views.actualizar_password_usuario, name='actualizar_password_usuario'),

    path('reportes/', views.reportes, name='reportes'),
    path('exportar-reporte-excel/', views.exportar_reporte_excel, name='exportar_reporte_excel'),
   
    # PERSONAL DE SEGURIDAD
    path('guardas/', views.guardas, name='guardas'),
    path('crear-guarda-seguridad/', views.crear_guarda_seguridad, name='crear_guarda_seguridad'),
    path('actualizar-guarda-seguridad/', views.actualizar_guarda_seguridad, name='actualizar_guarda_seguridad'),
    path('actualizar-password-guarda/', views.actualizar_password_guarda, name='actualizar_password_guarda'),
    path('eliminar-guarda-seguridad/', views.eliminar_guarda_seguridad, name='eliminar_guarda_seguridad'),

    #dasbor comedor
    path('comedor/', views.dashboard_comedor, name='dashboard_comedor'),

    #Reporte Internado 
    path('reporte-internado/', views.reporte_internado, name='reporte_internado'),
    path('exportar-reporte-internado-excel/', views.exportar_reporte_internado_excel, name='exportar_reporte_internado_excel'),


    path('internado/', views.dashboard_internado, name='dashboard_internado'),


]