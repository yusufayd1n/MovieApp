package com.example.movieapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.movieapp.ui.feature.detail.DetailScreen
import com.example.movieapp.ui.feature.favorites.FavoritesScreen
import com.example.movieapp.ui.feature.search.SearchScreen
import com.example.movieapp.ui.feature.settings.SettingsScreen
import com.example.movieapp.ui.navigation.screen.Screen

@Composable
fun MovieNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Search,
        modifier = modifier
    ) {
        composable<Screen.Search> {
            SearchScreen(
                onNavigateToDetail = { movieId ->
                    navController.navigate(Screen.Detail(movieId))
                }
            )
        }

        composable<Screen.Favorites> {
            FavoritesScreen()
        }

        composable<Screen.Settings> {
            SettingsScreen()
        }

        composable<Screen.Detail> {
            DetailScreen(onNavigateUp = { navController.navigateUp() })
        }
    }
}