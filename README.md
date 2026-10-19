# ShareTicket

App Android para gestionar tus tickets, hecha con **Kotlin** y **Jetpack Compose**.

## Funcionalidades

- Login con Google
- Lista de tus tickets
- Crear un ticket nuevo
- Ver, editar, marcar como completado y eliminar un ticket
- Pestaña de usuario para cerrar sesión

## Tecnologías

- Kotlin
- Jetpack Compose
- Firebase Authentication (Google)
- Cloud Firestore
- Arquitectura MVVM (Screen, ViewModel, UiState)

## Estructura del proyecto

```
com.shareticket.app
├── MainActivity.kt
├── data
│   ├── model          → Ticket.kt
│   └── repository     → AuthRepository.kt, TicketRepository.kt
├── navigation         → AppNavigation.kt
└── ui
    ├── components     → TicketItem.kt
    ├── screens
    │   ├── login      → Screen, ViewModel, UiState
    │   ├── tickets    → Screen, ViewModel, UiState
    │   ├── detail     → Screen, ViewModel, UiState
    │   └── user       → Screen, ViewModel
    └── theme
```

Más detalle en [ESTRUCTURA.md](ESTRUCTURA.md).

## Puesta en marcha

1. Clona el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/ShareTicket.git
   ```
2. Abre el proyecto en Android Studio.
3. Crea un proyecto en [Firebase](https://console.firebase.google.com) y añade la app Android con el paquete `com.shareticket.app`.
4. Activa **Google** como proveedor en Authentication y crea la base de datos en **Firestore**.
5. Añade tu huella **SHA-1** en la configuración de la app en Firebase.
6. Descarga `google-services.json` y colócalo en la carpeta `app/`.
7. Ejecuta la app.

> `google-services.json` no se sube al repositorio (cada persona usa el suyo o se lo pasa el equipo).

## Autores

- Jordi
- Iván
