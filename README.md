# 🎌 AkibaZone Mobile

AkibaZone es una aplicación móvil Android enfocada en la gestión, exploración y reproducción de contenido de anime.

El proyecto está desarrollado de forma nativa para Android utilizando **Kotlin**, **Jetpack Compose** y **Material 3**, con una arquitectura moderna orientada a mantener el código organizado, escalable y fácil de mantener.

---

## 📱 Descripción

AkibaZone busca ofrecer una experiencia cómoda y moderna para usuarios interesados en anime, permitiendo consultar información, explorar contenido, guardar favoritos, llevar seguimiento de episodios y reproducir contenido mediante un reproductor integrado.

La aplicación utiliza una interfaz oscura inspirada en plataformas modernas de streaming, con una identidad visual basada principalmente en tonos violeta, azul y fondos oscuros.

---

## ✨ Características

- 🔐 Autenticación de usuarios
- 🏠 Pantalla principal con contenido destacado
- 🔥 Anime en tendencia
- ⭐ Anime populares
- 🔎 Búsqueda de anime
- 🎭 Filtros por género
- 📖 Información detallada de cada anime
- 📺 Visualización de episodios
- ❤️ Sistema de favoritos
- ▶️ Continuar viendo
- 📜 Historial de reproducción
- 👤 Perfil de usuario
- ⚙️ Configuración
- 🔔 Notificaciones
- 🎬 Reproductor de video integrado

---

## 🛠️ Tecnologías utilizadas

### Lenguaje

- Kotlin

### Desarrollo Android

- Android Studio
- Jetpack Compose
- Material 3
- AndroidX
- Navigation Compose

### Arquitectura

- MVVM
- Clean Architecture
- Repository Pattern

### Programación asíncrona

- Kotlin Coroutines
- Flow
- StateFlow

### Networking

- Retrofit
- OkHttp

### Multimedia

- AndroidX Media3
- ExoPlayer

### Imágenes

- Coil

### Almacenamiento local

- Room
- DataStore

### Backend

Dependiendo de la configuración del proyecto:

- Supabase
- Firebase

### API de anime

El proyecto puede utilizar servicios externos para obtener información relacionada con anime, como:

- AniList GraphQL API

---

## 🎨 Paleta de colores

La interfaz de AkibaZone utiliza una estética oscura para reducir la fatiga visual y ofrecer una experiencia cómoda durante sesiones prolongadas.

| Uso | Color |
|---|---|
| Fondo principal | `#0B0D14` |
| Fondo secundario | `#111522` |
| Tarjetas | `#171B2A` |
| Superficies | `#1E2435` |
| Color principal | `#8B5CF6` |
| Violeta claro | `#A78BFA` |
| Azul secundario | `#38BDF8` |
| Texto principal | `#F1F5F9` |
| Texto secundario | `#94A3B8` |
| Favoritos | `#F472B6` |
| Éxito | `#34D399` |

---

## 🏗️ Arquitectura del proyecto

El proyecto utiliza una arquitectura basada en:

```text
UI
 ↓
ViewModel
 ↓
UseCase
 ↓
Repository
 ↓
Data Source
 ↓
API / Base de datos