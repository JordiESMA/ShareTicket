# ShareTicket

ShareTicket es una aplicación Android para crear listas de la compra (llamadas «tickets» en la app), añadir artículos y compartirlas con otras personas. Es un proyecto de prácticas hecho con Kotlin y Jetpack Compose.

## Qué se puede hacer

- Iniciar sesión con una cuenta de Google mediante Firebase Authentication.
- Crear tickets y consultar los que están compartidos con tu correo.
- Añadir artículos a un ticket, organizarlos por categoría, marcarlos como completados y eliminarlos.
- Compartir tickets con otros usuarios usando su correo o el alias que les hayas puesto.
- Enviar y responder solicitudes de amistad, consultar la lista de amigos y guardar alias.
- Cerrar sesión y volver a iniciar sesión más adelante.

La pantalla permite actualizar manualmente los datos con los botones de refresco. En el código no se usan listeners de Firestore para mantener las listas sincronizadas automáticamente.

## Tecnologías

- **Kotlin** como lenguaje.
- **Jetpack Compose** y **Material 3** para construir la interfaz.
- **Navigation Compose** para moverse entre inicio de sesión, lista y detalle.
- **Firebase Authentication** con Google Credential Manager para el acceso.
- **Cloud Firestore** para guardar usuarios, amistades, tickets y artículos.
- **Coroutines** para las operaciones asíncronas con Firebase.
- **Gradle Kotlin DSL** para configurar la compilación.

## Estructura del proyecto

```text
ShareTicket/
├── app/
│   └── src/
│       ├── main/                         # Código y recursos de la aplicación
│       ├── test/                         # Pruebas unitarias
│       └── androidTest/                  # Pruebas instrumentadas
├── gradle/                               # Wrapper y catálogo de versiones
├── build.gradle.kts                      # Configuración general de Gradle
├── gradlew / gradlew.bat                 # Gradle Wrapper para Linux/macOS y Windows
└── settings.gradle.kts                   # Configuración de módulos
```
## Requisitos

- Android Studio con soporte para el Android Gradle Plugin del proyecto.
- JDK 25 para el Gradle Wrapper configurado en el proyecto.
- Android SDK; la app declara `minSdk 24` y `compileSdk 37`.
- Un proyecto de Firebase con **Authentication (Google)** y **Cloud Firestore** configurados.
- Un dispositivo Android o emulador para ejecutar la aplicación.

## Instalación y configuración

1. Clona el repositorio (sustituye el enlace cuando lo tengas):

   ```bash
   git clone [PENDIENTE: URL DEL REPOSITORIO]
   cd ShareTicket
   ```

2. Abre la carpeta del proyecto en Android Studio y deja que Gradle termine la sincronización.
3. En Firebase, registra una aplicación Android con el identificador `com.shareticket.app` y activa Google como proveedor de inicio de sesión.
4. Crea Cloud Firestore y descarga el archivo `google-services.json` de esa aplicación. Colócalo en `app/google-services.json`.
5. Configura las huellas de firma que correspondan en Firebase, incluida la SHA-1 para el acceso con Google. Para una compilación de depuración, puedes consultar la huella con `./gradlew signingReport` (en Windows, `gradlew.bat signingReport`).

El proyecto contiene actualmente `app/google-services.json`. Si ese archivo no corresponde a tu proyecto Firebase, reemplázalo por el tuyo antes de compilar. Evita publicar credenciales o configuraciones privadas en un repositorio público.

## Ejecutar

Desde Android Studio, selecciona un emulador o conecta un dispositivo y pulsa **Run**.

También puedes compilar la APK de depuración desde la terminal:

```bash
# macOS / Linux
./gradlew assembleDebug

# Windows (PowerShell o CMD)
./gradlew.bat assembleDebug
```

La APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. Para instalarla en un dispositivo conectado con ADB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Al abrir la app, inicia sesión con Google. Desde **Mis Tickets**, crea una lista y ábrela para añadir artículos. Para compartirla, añade primero a la otra persona desde el menú de amigos y luego usa la opción de compartir con su correo o alias.

## Lo que he practicado

Con este proyecto he practicado Kotlin y Compose para crear varias pantallas, navegación entre ellas, gestión de estado y operaciones asíncronas con coroutines. También he trabajado con Firebase Authentication y Firestore, incluyendo documentos, consultas, subcolecciones y listas compartidas entre usuarios.

## Autoría

- Autor: [PENDIENTE]
- Repositorio: [PENDIENTE]

