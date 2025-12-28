package com.example.movieapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.movieapp.ui.feature.detail.DetailScreen
import com.example.movieapp.ui.feature.favorites.FavoriteListDetailScreen
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
            FavoritesScreen(
                onListClick = { listId ->
                    navController.navigate(Screen.FavoriteListDetail(listId))
                }
            )
        }

        composable<Screen.FavoriteListDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.FavoriteListDetail>()

            FavoriteListDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(onRegisterClick = {}, onLoginClick = {})
        }

        composable<Screen.Detail> {
            DetailScreen(onNavigateUp = { navController.navigateUp() })
        }
    }
}