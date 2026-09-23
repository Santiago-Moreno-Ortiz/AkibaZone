# Picture-in-Picture en AkibaZone

## Auditoría inicial

- Rama inicial: feature/reproduccion, HEAD 058b4b7; incluye 2734af1
  (`feat: agregar episodios y fuentes de reproduccion`).
- MainActivity era launcher/exported, sin soporte PIP ni configChanges.
- minSdk 24, targetSdk/compileSdk 37; Media3 1.4.1.
- PlayerScreen crea ExoPlayer con remember(context) y lo libera en onDispose.
- Antes, el observador del ciclo de vida del destino pausaba en ON_STOP;
  Home enviaba la app al fondo sin PIP.
- La navegación reconoce player/{episodeId}; la Activity no conocía el player.
- Ready contiene una PlaybackSource resuelta, mientras EXTERNAL sale por
  ACTION_VIEW desde Detail sin navegar a PlayerScreen. Los episodios heredados
  con tipo null solo pueden habilitar PIP tras resolver una fuente directa real.

## Implementación

MainActivity declara supportsPictureInPicture y maneja los cambios screenSize,
smallestScreenSize, screenLayout y orientation para conservar composición y
reproductor durante la transición. No hay nuevos permisos ni servicios.

La Activity comprueba API >= 26, soporte PIP del dispositivo y destino Player.
PlayerScreen comunica elegibilidad en cada evento del player y cambio de UI:
Ready, fuente HTTP(S) HLS/MP4 válida, MediaItem coincidente e isPlaying=true.
Esto excluye Loading, NoSource, Error, pausa, buffering, final e inactividad.
El tipo DIRECT_STREAM se asigna a esta evaluación únicamente al existir Ready;
la política además rechaza explícitamente EXTERNAL y tipos desconocidos.
No se modifican las fuentes ni el flujo ACTION_VIEW.

API 26–30: onUserLeaveHint llama enterPictureInPictureMode con los requisitos
anteriores. API >= 31: setAutoEnterEnabled se actualiza con la elegibilidad.
API 24–25 y dispositivos sin soporte: reproducción normal sin llamadas a PIP.
Se utiliza una relación fija 16:9. El sistema y la preferencia PIP del usuario
pueden impedir la entrada.

El observador ahora usa el ciclo de vida de la Activity: PIP visible mantiene
la Activity STARTED, por lo que no se pausa en ON_PAUSE. ON_STOP pausa cuando
la Activity deja de ser visible, incluido cerrar PIP. La misma instancia de
ExoPlayer permanece al expandir PIP. Atrás (sistema o botón) deshabilita PIP y
pausa antes de navegar; onDispose elimina listeners y libera el player.
La ruta también se comprueba directamente con un listener de navegación.
No se ofrece reproducción de audio en segundo plano ni recuperación tras
muerte del proceso. Cerrar PIP no garantiza destruir inmediatamente la Activity:
si Android la conserva, el reproductor queda pausado hasta volver o disponerla.

En PIP se ocultan controles de PlayerView, botón Atrás y márgenes del Scaffold.
Al expandir se restaura la interfaz. No se añaden acciones PIP personalizadas.

## Verificación manual pendiente

Usar un dispositivo/emulador con PIP habilitado. Repetir en API 26–30 y >=31,
con Home por botón y gesto cuando estén disponibles. Usar una fuente real ya
accesible al flujo de la app; no añadir URLs de prueba ni modificar proveedores.

### A — Home y regreso

1. Abrir AkibaZone y un anime con DIRECT_STREAM válido disponible.
2. Abrir el capítulo y confirmar video en movimiento en PlayerScreen.
3. Presionar Home: debe aparecer PIP y continuar video/audio.
4. Tocar la ventana y usar la opción de expandir del sistema si es necesaria.
5. Confirmar regreso a PlayerScreen sin reinicio ni duplicación del audio.
6. Verificar restauración de controles; cerrar PIP y comprobar que cesa el audio.

### B — Atrás

1. Reproducir el capítulo.
2. Presionar Atrás del sistema; repetir con el botón de PlayerScreen.
3. Confirmar Detail, cese del audio y ausencia de PIP.
4. Presionar Home desde Detail: tampoco debe activar PIP.

### C — EXTERNAL

1. Abrir un episodio AniList EXTERNAL disponible.
2. Confirmar ACTION_VIEW hacia navegador/proveedor.
3. Confirmar que AkibaZone no crea PIP. El proveedor puede tener su propio PIP.

### D — Sin fuente/error

1. Abrir un episodio sin fuente disponible o provocar un fallo real de conexión.
2. Confirmar NoSource/Error y presionar Home: sin PIP ni crash.
3. Repetir mientras Loading y con el reproductor pausado o finalizado.

### Compatibilidad y aislamiento

- En API 24–25: reproducir, usar Home y Atrás; sin PIP ni crash.
- En API >=26: deshabilitar PIP desde Ajustes Android; Home no debe causar crash.
- Home desde Home, Explorar, Detail, Favoritos, Historial, Perfil y Configuración
  no debe crear PIP, incluso después de abandonar una reproducción.
- Rotar durante reproducción/PIP y verificar continuidad y controles.

No se ha validado un DIRECT_STREAM real disponible ni realizado estas pruebas
instrumentadas en esta sesión.

Implementación preparada, prueba PIP real pendiente por falta de DIRECT_STREAM disponible.

## Referencias

- https://developer.android.com/develop/ui/compose/system/pip-setup
- https://developer.android.com/develop/ui/compose/system/pip-enter
- https://developer.android.com/develop/ui/views/picture-in-picture

## Demostración local

Configuración → Demostración → Probar reproductor y PIP abre `demo-player`.
Esta ruta usa `PlayerInput.Demo`; no es un ID de anime ni consulta el repositorio.
Reutiliza PlayerScreen, PlayerViewModel y la instancia habitual de ExoPlayer.
Fuera de PIP se muestra «Video de demostración».

Debes colocar un MP4 autorizado en app/src/main/res/raw/demo_video.mp4

El recurso es opcional para compilar: se busca por nombre dentro del paquete
actual con Resources.getIdentifier. Si no existe se muestra un error controlado
sin reproducción ni PIP. Tras añadirlo, recompilar e instalar la aplicación.
No se incluye ni descarga ningún video con esta funcionalidad.

DemoVideo encapsula la URI android.resource://<package>/<resource_id> resuelta
localmente. PIP permite esta excepción solo con esa capacidad explícita, formato
MP4, Ready, MediaItem coincidente y reproducción activa. Una URI local arbitraria
no se acepta como demo; EXTERNAL continúa rechazado. Las reglas HTTP(S) anteriores
siguen aplicándose a las fuentes normales. Si se habilita resource shrinking en
el futuro, debe preservarse el recurso opcional que se busca por nombre.

Pruebas manuales pendientes hasta disponer del MP4 autorizado:

1. Configuración → Probar reproductor y PIP: verificar video y play/pause.
2. Durante reproducción, Home: verificar ventana PIP y continuidad del video.
3. Expandir PIP: verificar regreso al mismo PlayerScreen y controles restaurados.
4. Atrás (botón y sistema): volver a Configuración sin PIP y sin audio residual.
5. Abrir streamingEpisode EXTERNAL: proveedor externo, sin demo ni PlayerScreen
   ni PIP de AkibaZone.
6. Sin recurso: mensaje de ausencia, sin crash ni PIP. Con MP4 inválido: error
   de reproducción controlado, sin PIP.
