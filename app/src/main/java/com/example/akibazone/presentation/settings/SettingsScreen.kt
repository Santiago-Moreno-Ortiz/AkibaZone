package com.example.akibazone.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akibazone.BuildConfig
import com.example.akibazone.data.preferences.ThemePreference
import com.example.akibazone.ui.theme.Background
import com.example.akibazone.ui.theme.BackgroundSecondary
import com.example.akibazone.ui.theme.Error
import com.example.akibazone.ui.theme.TextPrimary
import com.example.akibazone.ui.theme.TextSecondary

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onDemoClick: () -> Unit) {
    val themePreference by viewModel.themePreference.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Apariencia",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tema de la aplicación",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(Modifier.height(4.dp))

        ThemeOption("Sistema", ThemePreference.SYSTEM, themePreference, viewModel::setTheme)
        ThemeOption("Claro", ThemePreference.LIGHT, themePreference, viewModel::setTheme)
        ThemeOption("Oscuro", ThemePreference.DARK, themePreference, viewModel::setTheme)

        errorMessage?.let {
            Text(
                text = it,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(28.dp))
        HorizontalDivider(color = BackgroundSecondary)
        Spacer(Modifier.height(28.dp))

        Text("Demostración", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Prueba el reproductor interno y Picture-in-Picture.", color = TextSecondary)
        Spacer(Modifier.height(12.dp))
        androidx.compose.material3.Button(onClick = onDemoClick) {
            Text("Probar reproductor y PIP")
        }
        Spacer(Modifier.height(28.dp))
        HorizontalDivider(color = BackgroundSecondary)
        Spacer(Modifier.height(28.dp))

        Text(
            text = "Acerca de AkibaZone",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "AkibaZone",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Aplicación académica para consultar y organizar anime.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Versión ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun ThemeOption(
    label: String,
    option: ThemePreference,
    selected: ThemePreference,
    onSelected: (ThemePreference) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(option) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = option == selected,
            onClick = { onSelected(option) }
        )
        Text(text = label, color = TextPrimary)
    }
}
