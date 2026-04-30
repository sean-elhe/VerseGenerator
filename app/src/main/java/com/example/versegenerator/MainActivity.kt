package com.example.versegenerator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.versegenerator.SelectionScreen.SelectionScreen
import com.example.versegenerator.ViewModels.ThemeConfig
import com.example.versegenerator.ViewModels.VerseViewModel
import com.example.versegenerator.data.AppDatabase
import com.example.versegenerator.ui.theme.VerseGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK)
        )

        setContent {
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }

            val viewModel: VerseViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        VerseViewModel(
                            db.bibleDao(),
                            application
                        )
                    }
                }
            )
            val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()

//            VerseGeneratorTheme(themeConfig) {
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    AppNavigation(viewModel, themeConfig)
//                }
            VerseGeneratorTheme(themeConfig) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF7298C7))
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(viewModel, themeConfig)
                    }
                }
            }
                }
            }
        }

@Composable
fun AppNavigation (viewModel: VerseViewModel, themeConfig: ThemeConfig) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "second") {
        composable(route = "second") {
            SelectionScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )
        }
    }
}