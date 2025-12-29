package com.example.movieapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movieapp.ui.feature.auth.login.LoginScreen
import com.example.movieapp.ui.feature.auth.register.RegisterScreen
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
                onNavigate = { screen ->
                    navController.navigate(screen)
                }
            )
        }

        composable<Screen.Favorites> {
            FavoritesScreen(
                onNavigate = { screen ->
                    navController.navigate(screen)
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.FavoriteListDetail> {
            FavoriteListDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(onNavigate = { screen ->
                navController.navigate(screen)
            })
        }

        composable<Screen.Detail> {
            DetailScreen(onNavigateUp = { navController.navigateUp() })
        }

        composable<Screen.Login> {
            LoginScreen(
                onNavigate = {
                    navController.navigate(Screen.Register)
                },
                onLoginSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Register> {
            RegisterScreen(
                onNavigate = { screen ->
                    if (screen == Screen.Login) {
                        navController.navigate(screen) {
                            popUpTo(Screen.Register) { inclusive = true }
                        }
                    } else {
                        navController.navigate(screen)
                    }
                }
            )
        }
    }
}