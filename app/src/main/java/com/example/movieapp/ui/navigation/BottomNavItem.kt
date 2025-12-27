package com.example.movieapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.movieapp.R
import com.example.movieapp.ui.navigation.screen.Screen

sealed class BottomNavItem<T : Screen>(
    val route: T,
    val icon: ImageVector,
    val titleResId: Int
) {
    data object Home : BottomNavItem<Screen.Search>(Screen.Search, Icons.Default.Home, R.string.nav_home)
    data object Favorites : BottomNavItem<Screen.Favorites>(Screen.Favorites, Icons.Default.Favorite, R.string.nav_favorites)
    data object Settings : BottomNavItem<Screen.Settings>(Screen.Settings, Icons.Default.Settings, R.string.nav_settings)
}