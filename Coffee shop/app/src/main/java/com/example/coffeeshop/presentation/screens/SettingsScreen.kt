package com.example.coffeeshop.presentation.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.automirrored.twotone.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.twotone.Build
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.Email
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.navigation.NavigationRoutes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefsManager = remember { PrefsManager(context) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Информация о пользователе
            UserInfoSection(prefsManager)

            // Разделитель
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Настройки приложения
            SettingsOptionsSection(
                prefsManager = prefsManager,
                navController = navController,
                context = context
            )
        }
    }
}

@Composable
private fun UserInfoSection(prefsManager: PrefsManager) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Профиль",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Column {
                    Text(
                        text = "Профиль",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val userName = prefsManager.getName() ?: "Пользователь"
                    val userEmail = prefsManager.getEmail() ?: "email@example.com"

                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsOptionsSection(
    prefsManager: PrefsManager,
    navController: NavController,
    context: Context
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Уведомления
    SettingsItem(
        title = "Уведомления",
        subtitle = "Включить/выключить уведомления",
        leadingIcon = Icons.Default.Notifications,
        trailingContent = {
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }
    )

    // Темная тема
    SettingsItem(
        title = "Темная тема",
        subtitle = "Включить темный режим",
        leadingIcon = Icons.TwoTone.Build,
        trailingContent = {
            Switch(
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )
        }
    )

    // История заказов
    SettingsItem(
        title = "История заказов",
        subtitle = "Просмотр ваших заказов",
        leadingIcon = Icons.TwoTone.DateRange,
        onClick = {
            // TODO: Реализовать переход на экран истории заказов
        }
    )

    // Связаться с нами
    SettingsItem(
        title = "Связаться с нами",
        subtitle = "Поддержка и обратная связь",
        leadingIcon = Icons.TwoTone.Email,
        onClick = {
            // TODO: Реализовать переход на экран контактов
        }
    )

    // О приложении
    SettingsItem(
        title = "О приложении",
        subtitle = "Версия 1.0.0",
        leadingIcon = Icons.TwoTone.Info,
        onClick = {
            // TODO: Реализовать экран информации о приложении
        }
    )

    // Разделитель перед выходом
    Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

    SettingsItem(
        title = "Выйти из аккаунта",
        subtitle = "Завершить текущую сессию",
        leadingIcon = Icons.AutoMirrored.TwoTone.ExitToApp,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        onClick = {
            showLogoutDialog = true
        }
    )

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Выход из аккаунта") },
            text = { Text("Вы уверены, что хотите выйти из аккаунта?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        prefsManager.logout()
                        navController.navigate(NavigationRoutes.SIGN_IN) {
                            popUpTo(NavigationRoutes.HOME) {
                                inclusive = true
                            }
                        }
                        showLogoutDialog = false
                    }
                ) {
                    Text("Выйти", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        onClick = { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Левая часть: иконка и текст
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = title,
                        modifier = Modifier.size(24.dp),
                        tint = contentColor
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = contentColor
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Правая часть: переключатель или стрелка
            trailingContent?.invoke() ?: run {
                if (onClick != null) {
                    Icon(
                        imageVector = Icons.AutoMirrored.TwoTone.ArrowForward,
                        contentDescription = "Перейти",
                        modifier = Modifier.size(16.dp),
                        tint = contentColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}