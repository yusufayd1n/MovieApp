package com.example.movieapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.ui.components.BottomNavigationBar
import com.example.movieapp.ui.navigation.MovieNavHost
import com.example.movieapp.ui.navigation.screen.Screen
import com.example.movieapp.ui.theme.MovieAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.movieapp.common.LocaleHelper
import com.example.movieapp.data.repository.APP_LANGUAGE
import com.example.movieapp.data.repository.SettingsRepository
import com.example.movieapp.data.repository.dataStore
import com.example.movieapp.ui.feature.settings.SettingsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            MovieAppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination


                val showBottomBar = currentDestination?.hasRoute<Screen.Search>() == true ||
                        currentDestination?.hasRoute<Screen.Favorites>() == true ||
                        currentDestination?.hasRoute<Screen.Settings>() == true
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController = navController)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        MovieNavHost(navController = navController)
                    }
                }
            }
        }
    }
}