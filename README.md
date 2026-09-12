# AkibaZone

Proyecto académico desarrollado en Android Studio como parte de una actividad de desarrollo móvil. Su objetivo es aplicar conceptos fundamentales del desarrollo Android moderno mediante una aplicación de consulta de anime.

## Funcionalidades

El código implementa:

- Inicio con secciones de catálogo, tendencias, títulos en emisión y mejor valorados.
- Búsqueda por título y filtros por género, formato y orden.
- Detalle con portada, sinopsis y metadatos disponibles.
- Guardado y eliminación de favoritos en el dispositivo con Room.
- Pantallas de carga, resultados y error, con reintento en inicio, búsqueda y detalle.
- Registro e inicio de sesión de demostración, con usuarios guardados únicamente en memoria.

La consulta del catálogo depende de servicios externos. La reproducción es parcial: el reproductor existe, pero AniList no entrega enlaces de video. El historial, la configuración y las estadísticas de perfil están pendientes; no se presentan como funciones completas.

## Tecnologías utilizadas

- Kotlin, Jetpack Compose y Material Design 3.
- MVVM, ViewModel, StateFlow y coroutines.
- Navigation Compose con NavHost y NavController.
- Retrofit y Gson para solicitudes HTTP y conversión de JSON.
- Room para favoritos locales; LiveData para observar los datos guardados.
- Coil para imágenes.
- Jsoup para el scraper alternativo existente.
- Media3 / ExoPlayer para el reproductor parcial.
- Gradle y Git como herramientas de construcción y control de versiones; el repositorio puede clonarse desde GitHub.

## Arquitectura

La organización sigue MVVM con una separación sencilla:

- **Model:** entidades locales, modelos, DTOs, servicios de red y `AnimeRepository`.
- **View:** pantallas y componentes Compose que muestran el estado y reciben acciones.
- **ViewModel:** carga datos mediante el repositorio o los casos de uso existentes y expone el estado a la interfaz.

La carpeta `domain/usecase` contiene operaciones como cargar el inicio, buscar y alternar favoritos. `MainViewModelFactory` crea los ViewModels y sus dependencias manualmente. No se utiliza Hilt ni se pretende presentar una arquitectura empresarial.

## Consumo de API

La fuente principal es [AniList GraphQL](https://docs.anilist.co/guide/graphql/), consultada mediante Retrofit en `https://graphql.anilist.co/`. Proporciona títulos, imágenes, sinopsis, géneros y otros metadatos. **Es GraphQL, no REST.**

Como respaldo se utiliza [Jikan REST](https://docs.api.jikan.moe/), una API pública que no requiere API key. El repositorio consulta sus endpoints de catálogo, búsqueda y detalle cuando AniList no está disponible. La aplicación no incluye credenciales privadas.

Existe un scraper de AnimeFLV como alternativa para algunas consultas. Depende de la estructura HTML externa y no garantiza resultados ni videos reproducibles. Los identificadores de esa fuente se mantienen separados de los numéricos de AniList.

Las solicitudes se ejecutan con funciones `suspend` y coroutines. Se comprueban los errores GraphQL, que también pueden aparecer en una respuesta HTTP exitosa. AniList no proporciona streams: la aplicación no inventa episodios o URLs cuando faltan esos datos.

**Validación del 11 de septiembre de 2026:** una consulta pública a AniList devolvió HTTP 403 y `data: null` con un error de deshabilitación temporal por problemas de estabilidad. En la misma revisión, Jikan respondió HTTP 200 para catálogo, búsqueda y detalle, por lo que queda como fallback operativo. La disponibilidad de servicios externos puede cambiar.

## Navegación

`MainActivity` contiene un `NavHost`; el `NavController` gestiona las transiciones. Las rutas están definidas en `navigation/Screen.kt`:

- `home`, `explore`, `favorites`, `history` y `profile`.
- `detail/{animeId}` y `player/{episodeId}` para destinos con argumentos.

Los argumentos se codifican para admitir enlaces con caracteres especiales. La barra inferior conserva el estado de las pestañas. `history` muestra un aviso de función pendiente; `settings` está declarado, pero no tiene pantalla implementada.

## Estructura del proyecto

```text
app/src/main/java/com/example/akibazone/
├── data/          # Room, modelos locales, red y repositorio
├── domain/        # Modelos usados por las pantallas y casos de uso
├── navigation/    # Rutas
├── presentation/  # Pantallas, ViewModels y estados
├── ui/            # Tema y componentes Compose
└── MainActivity.kt
app/src/main/res/  # Recursos Android activos
app/src/test/      # Pruebas locales
app/src/androidTest/ # Pruebas que requieren Android
docs/legacy-xml/  # Recursos de la interfaz anterior, fuera de la compilación
```

El proyecto y el nombre visible son **AkibaZone**. El package, namespace y applicationId son `com.example.akibazone`, coherentes con el nombre actual del proyecto. Solo existe el módulo `app`.

Los XML anteriores se conservan en `docs/legacy-xml` como referencia: apuntaban a fragments inexistentes y no eran utilizados por `MainActivity`. No representan una segunda navegación activa.

## Ejecución

1. Clonar este repositorio con Git y abrir su carpeta raíz en Android Studio.
2. Usar una versión de Android Studio compatible con el Android Gradle Plugin declarado en `gradle/libs.versions.toml` (9.3.2).
3. Instalar el SDK requerido por `compileSdk` y `targetSdk` (37). El dispositivo o emulador debe tener Android API 24 o superior.
4. Sincronizar Gradle. El wrapper utiliza Gradle 9.5.0 y los criterios del daemon solicitan JDK 25. Revisar que `local.properties` apunte al SDK de la máquina; ese archivo no se comparte en Git.
5. Seleccionar el módulo `app` y ejecutar en un emulador o dispositivo. El catálogo necesita conexión a Internet y disponibilidad de la API.

Para validar desde terminal, configurar `JAVA_HOME` y ejecutar:

```bash
./gradlew clean assembleDebug
./gradlew testDebugUnitTest lintDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`. Las pruebas instrumentadas requieren un dispositivo o emulador conectado.

En la revisión del 11 de septiembre de 2026 se ejecutó `clean` y la validación final de `assembleDebug`, `testDebugUnitTest` y `lintDebug` terminó correctamente: cinco pruebas locales aprobadas y Lint con cero errores y 30 advertencias. No había un dispositivo o emulador conectado, por lo que no se verificaron visualmente las pantallas ni se ejecutaron las pruebas instrumentadas.

## Estado del proyecto

Proyecto académico en desarrollo, preparado principalmente con fines educativos y de presentación.

Los favoritos son locales y no están asociados a una cuenta remota. El acceso es una demostración en memoria: no persiste usuarios al cerrar el proceso ni autentica contra un servidor. El reproductor solo admite enlaces multimedia compatibles; la integración de streaming permanece parcial. No hay historial funcional, estadísticas calculadas ni pantalla de configuración.

## Autores

Desarrollado para la Sustentación Final de Aplicaciones Android.

El README anterior no especificaba nombres de autores.
