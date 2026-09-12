package com.example.akibazone.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akibazone.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onFavoritesClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isRegisterMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        when (val state = uiState) {
            is AuthUiState.Unauthenticated -> LoginContent(
                isRegisterMode = isRegisterMode,
                onModeChange = { isRegisterMode = it },
                onLogin = viewModel::login,
                onRegister = viewModel::register
            )
            is AuthUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            is AuthUiState.Authenticated -> {
                ProfileContent(
                    userName = state.userName,
                    email = state.email,
                    onLogout = viewModel::logout,
                    onFavoritesClick = onFavoritesClick,
                    onHistoryClick = onHistoryClick
                )
            }
            is AuthUiState.Error -> {
                LoginContent(
                    isRegisterMode = isRegisterMode,
                    onModeChange = { isRegisterMode = it },
                    onLogin = viewModel::login,
                    onRegister = viewModel::register,
                    errorMessage = state.message
                )
                LaunchedEffect(state) {
                    delay(3000)
                    viewModel.clearError()
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    isRegisterMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isRegisterMode) Icons.Default.PersonAdd else Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (isRegisterMode) "Crea tu cuenta" else "Bienvenido a AkibaZone",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Acceso de demostración: los usuarios se conservan solo durante esta sesión de la app.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BackgroundSecondary,
                focusedLabelColor = Primary,
                cursorColor = Primary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BackgroundSecondary,
                focusedLabelColor = Primary,
                cursorColor = Primary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Error, style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { 
                if (isRegisterMode) onRegister(email, password)
                else onLogin(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isRegisterMode) "Registrarse" else "Entrar",
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { onModeChange(!isRegisterMode) }) {
            Text(
                text = if (isRegisterMode) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate aquí",
                color = Secondary
            )
        }
    }
}

@Composable
fun ProfileContent(
    userName: String,
    email: String,
    onLogout: () -> Unit,
    onFavoritesClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(SurfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(label = "Animes vistos", value = "—", modifier = Modifier.weight(1f))
            StatCard(label = "Episodios", value = "—", modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(label = "Horas vistas", value = "—", modifier = Modifier.weight(1f))
            StatCard(label = "Favoritos", value = "—", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("Estadísticas y configuración pendientes de implementar", color = TextSecondary)
        ProfileMenuItem(icon = Icons.Default.Settings, label = "Configuración (pendiente)")
        ProfileMenuItem(icon = Icons.Default.History, label = "Mi Historial", onClick = onHistoryClick)
        ProfileMenuItem(icon = Icons.Default.Favorite, label = "Mis Favoritos", onClick = onFavoritesClick)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BackgroundSecondary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cerrar sesión", color = Error, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = Primary, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}

/// Delay helper para operaciones asíncronas
 suspend fun delay(timeMillis: Long) {
    kotlinx.coroutines.delay(timeMillis)
}
