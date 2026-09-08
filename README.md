# AkibaZone Mobile

## Descripción

**AkibaZone Mobile** es una aplicación Android moderna y funcional para catálogo y streaming de anime, diseñada para ofrecer una experiencia premium de descubrimiento y reproducción de contenido anime. La aplicación permite a los usuarios explorar una amplia base de datos de anime, gestionar sus títulos favoritos y disfrutar de streaming de video mediante integración con APIs externas.

La aplicación está construida siguiendo la arquitectura Clean Architecture con patrón MVVM, utilizando Jetpack Compose para la interfaz de usuario y Material Design 3 para el sistema de diseño visual.

## Tecnologías

- **Kotlin**: 100% del código en Kotlin, aprovechando características modernas del lenguaje
- **Jetpack Compose**: Interfaz de usuario declarativa construida con Compose
- **Material Design 3**: Sistema de diseño completo con tema oscuro AkibaZone
- **MVVM**: Arquitectura Modelo-Vista-ViewModel con separación clara de responsabilidades
- **Navigation Compose**: Navegación declarativa con NavController y NavHost
- **Retrofit**: Consumo de APIs REST con endpoints definados y tipados
- **Coroutines**: Programación asíncrona con Kotlin Coroutines
- **StateFlow**: Estado reactivo para la capa de presentación
- **Room**: Base de datos local para persistencia de historial y favoritos
- **Coil**: Carga y caché de imágenes
- **Gson**: Parsing de respuestas JSON de API
- **Jsoup**: Web scraping para fuentes alternativas
- **ExoPlayer**: Reproducción de video streaming HLS

## Arquitectura

La aplicación sigue un patrón estricto de separación de responsabilidades:

```
Compose Screen
      ↓
ViewModel
      ↓
Repository
      ↓
API Service (Retrofit / Jimo API / AnimeFLV Scraper)
      ↓
API REST (AniList GraphQL, Jimo REST)
```

### Capas:

**MODEL:**
- Modelos de datos y DTOs para consumo de API
- Entidades Room para base de datos local
- Casos de uso (Use Cases) con lógica de negocio pura

**VIEW:**
- Pantallas desarrolladas con Jetpack Compose
- Componentes reutilizables (AnimeCard, LoadingView, ErrorView, etc.)
- Estados visuales y diseño de interfaz
- Sin lógica de negocio compleja

**VIEWMODEL:**
- Estado de cada pantalla mediante StateFlow
- Lógica de presentación y coordinación
- Llamadas a los repositorios
- Gestión de Loading, Success y Error estados

**REPOSITORY:**
- Centraliza el acceso a los datos
- Separa la UI de la implementación concreta de la API
- Coordina entre API remota y base de datos local

## Funcionalidades

### HOME:
- Banner o contenido destacado con anime en tendencia
- Lista de animes organizados en secciones (Populares, Últimos Estrenos, Tendencias)
- Cards visuales con imagen, título y calificación
- Navegación a pantalla de detalle

### CATÁLOGO:
- Mostrar animes obtenidos desde API (AniList como fuente principal)
- Cada tarjeta muestra: imagen, nombre, información básica
- Búsqueda de animes por título o género
- Navegación a pantalla de detalle al hacer clic

### DETALLE:
- Información completa del anime: imagen, nombre, sinopsis, géneros
- Información adicional: año, estado, tipo
- Botón de favoritos (agregar/eliminar de la lista personal)
- Botón de reproducción que navega al reproductor
- Recibe animeId mediante Navigation Compose arguments

### FAVORITOS:
- Lista de animes favoritos guardados localmente
- Poder agregar y eliminar favoritos
- Sincronización con base de datos Room
- Indicator visual de favorito

### PERFIL:
- Información básica del usuario
- Estadísticas de visualización (animes vistos, episodios, horas)
- Opciones: Configuración, Mi Historial, Mis Favoritos
- Cerrar sesión

## API

La aplicación consume múltiples fuentes de datos:

**AniList API (Primary):**
- GraphQL API en `https://graphql.anilist.co/`
- Consulta para obtener animes populares, últimos lanzamientos y búsqueda
- Datos confiables y actualizados sobre anime

**Jimo API (Alternative):**
- REST API en `https://jimov.herokuapp.com/`
- Endpoints para filtrar, buscar y obtener información de anime
- Fallback cuando Anilist no está disponible

**AnimeFLV Scraper (Fallback):**
- Web scraping de `https://www3.animeflv.net`
- Obtiene información cuando las APIs principales fallan o no tienen el contenido

El consumo de red se realiza mediante funciones suspend y coroutines, nunca bloqueando el hilo principal.

## Navegación

La aplicación utiliza Navigation Compose con las siguientes rutas principales:

```
Home
  ├── Explorar (search)
  ├── Catálogo (listado completo)
  ├── DetalleAnime/{animeId}  (recibe argumento)
  ├── Favoritos
  └── Perfil (auth flow)
```

### Rutas definidas en `Screen.kt`:
- `home` - Pantalla principal
- `explore` - Búsqueda y exploración
- `detail/{animeId}` - Detalle del anime (recibe animeId por argumento)
- `favorites` - Favoritos del usuario
- `profile` - Perfil de usuario y auth
- `player/{episodeId}` - Reproductor de video

Los argumentos de navegación se pasan mediante `NavController.navigate()` con rutas con parámetros como `detail/{animeId}`.

## Estados

Cada pantalla que consume información de Internet maneja correctamente estos estados:

**Loading:** Mostrar CircularProgressIndicator con color Primary. La pantalla muestra un indicador de carga animado mientras se obtienen los datos.

**Success:** Mostrar los datos obtenidos. La UI reacciona mostrando el contenido correspondiente (lista de animes, detalle, perfil, etc.).

**Error:** Mostrar mensaje amigable y opción para reintentar. Nunca deja la pantalla en blanco cuando una API falla. Los errores incluyen:
- Sin internet
- Error HTTP
- Respuesta vacía
- Error de parsing
- API no disponible

El patrón UiState sellado es:

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

## Capturas

La aplicación incluye capturas de las principales pantallas:
- HomeScreen con banner y categorías
- ExploreScreen con resultados de búsqueda
- AnimeDetailScreen con información completa
- FavoritesScreen con lista de favoritos
- ProfileScreen con autenticación y estadísticas

## Instalación

1. Clonar el repositorio
2. Abrir con **Android Studio Ladybug (o superior)**
3. Sincronizar Gradle (`Project` -> `Sync Now`)
4. Ejecutar en un emulador con API 24+ o dispositivo físico
5. La aplicación requiere permisos de Internet y red

## Autor

Desarrollado para la Sustentación Final de Aplicaciones Android.

---
**AkibaZone Mobile** - Tu plataforma de anime en Android