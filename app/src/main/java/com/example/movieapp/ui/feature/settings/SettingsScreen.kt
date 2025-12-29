package com.example.movieapp.ui.feature.settings

import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieapp.R
import com.example.movieapp.common.ui.ObserveAsEvents
import com.example.movieapp.common.ui.UiEvent
import com.example.movieapp.ui.navigation.screen.Screen
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showLanguageDialog by remember { mutableStateOf(false) }

    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowSnackbar -> {
                scope.launch {
                    val message = event.remoteMessage ?: context.getString(event.messageResId)
                    snackbarHostState.showSnackbar(message)
                }
            }

            is UiEvent.Navigate -> {
                onNavigate(event.screen)
            }

            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.nav_settings),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            SettingsSectionTitle(text = stringResource(R.string.appearance_title))
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (state.isDarkTheme) Icons.Default.Delete else Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(stringResource(R.string.dark_mode_label))
                    }
                    Switch(
                        checked = state.isDarkTheme,
                        onCheckedChange = { viewModel.updateTheme(it) }
                    )
                }
            }

            SettingsSectionTitle(text = stringResource(R.string.language_region_title))
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(R.string.app_language_label),
                    subtitle = stringResource(R.string.current_language_name),
                    onClick = { showLanguageDialog = true }
                )
            }

            SettingsSectionTitle(text = stringResource(R.string.account_title))

            if (state.currentUser != null) {
                SettingsCard {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Person,
                            title = state.currentUser?.email
                                ?: stringResource(R.string.default_user_name),
                            subtitle = stringResource(R.string.logged_in_status),
                            onClick = {}
                        )
                        HorizontalDivider()
                        SettingsItem(
                            icon = Icons.Default.Lock,
                            title = stringResource(R.string.change_password_action),
                            onClick = { viewModel.showPasswordDialog() }
                        )
                        HorizontalDivider()
                        SettingsItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            title = stringResource(R.string.logout_action),
                            titleColor = MaterialTheme.colorScheme.error,
                            onClick = { viewModel.signOut() }
                        )
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.login_promo_text),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = { onNavigate(Screen.Login) }) {
                                Text(stringResource(R.string.login_button))
                            }
                            OutlinedButton(onClick = { onNavigate(Screen.Register) }) {
                                Text(stringResource(R.string.register_button))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                appVersion?.let {
                    Text(
                        text = stringResource(R.string.version_format, appVersion),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = state.currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { newLanguageCode ->
                viewModel.updateLanguage(newLanguageCode)
                showLanguageDialog = false
                if (context is Activity) {
                    context.recreate()
                }
            }
        )
    }

    if (state.isPasswordDialogVisible) {
        ChangePasswordDialog(
            onDismiss = { viewModel.hidePasswordDialog() },
            onConfirm = { oldPass, newPass ->
                viewModel.changePassword(oldPass, newPass)
            }
        )
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_password_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text(stringResource(R.string.current_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.new_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(currentPassword, newPassword) }) {
                Text(stringResource(R.string.update_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_action))
            }
        }
    )
}

@Composable
fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.app_language_label)) },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected("tr") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentLanguage == "tr",
                        onClick = { onLanguageSelected("tr") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.language_turkish))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected("en") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentLanguage == "en",
                        onClick = { onLanguageSelected("en") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.language_english))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_dialog_action))
            }
        }
    )
}