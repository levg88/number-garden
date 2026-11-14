package com.numbergarden.app.ui.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.ui.screens.CompareScreen
import com.numbergarden.app.ui.screens.CountingScreen
import com.numbergarden.app.ui.screens.GamesScreen
import com.numbergarden.app.ui.screens.AdditionScreen
import com.numbergarden.app.ui.screens.MatchScreen
import com.numbergarden.app.ui.screens.MissingScreen
import com.numbergarden.app.ui.screens.PatternScreen
import com.numbergarden.app.ui.screens.ShapesScreen
import com.numbergarden.app.ui.screens.TapNumberScreen
import com.numbergarden.app.ui.screens.GardenScreen
import com.numbergarden.app.ui.screens.GrownUpsGateScreen
import com.numbergarden.app.ui.screens.HomeScreen
import com.numbergarden.app.ui.screens.LessonPlayerScreen
import com.numbergarden.app.ui.screens.NumbersScreen
import com.numbergarden.app.ui.screens.SettingsScreen
import com.numbergarden.app.ui.screens.WatchScreen

private object Routes {
    const val HOME = "home"
    const val WATCH = "watch"
    const val COUNT = "count"
    const val COMPARE = "compare"
    const val NUMBERS = "numbers"
    const val GAMES = "games"
    const val MATCH = "match"
    const val TAP_NUMBER = "tapnumber"
    const val PATTERN = "pattern"
    const val ADDITION = "addition"
    const val SHAPES = "shapes"
    const val MISSING = "missing"
    const val GARDEN = "garden"
    const val GATE = "grownups"
    const val SETTINGS = "settings"
    const val PLAYER = "player/{lessonId}"
    fun player(lessonId: String) = "player/$lessonId"
}

@Composable
fun NumberGardenNavHost(container: AppContainer) {
    val navController = rememberNavController()

    val goHome: () -> Unit = {
        if (!navController.popBackStack(Routes.HOME, inclusive = false)) {
            navController.navigate(Routes.HOME) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                container = container,
                onWatch = { navController.navigate(Routes.WATCH) },
                onCount = { navController.navigate(Routes.COUNT) },
                onCompare = { navController.navigate(Routes.COMPARE) },
                onNumbers = { navController.navigate(Routes.NUMBERS) },
                onPlay = { navController.navigate(Routes.GAMES) },
                onGarden = { navController.navigate(Routes.GARDEN) },
                onGrownUps = { navController.navigate(Routes.GATE) },
            )
        }

        composable(Routes.WATCH) {
            WatchScreen(
                container = container,
                onLesson = { lessonId -> navController.navigate(Routes.player(lessonId)) },
                onBack = { navController.popBackStack() },
                onHome = goHome,
            )
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType }),
        ) { entry ->
            LessonPlayerScreen(
                container = container,
                lessonId = entry.arguments?.getString("lessonId").orEmpty(),
                onBack = { navController.popBackStack() },
                onHome = goHome,
            )
        }

        composable(Routes.COUNT) {
            CountingScreen(container = container, onHome = goHome)
        }

        composable(Routes.COMPARE) {
            CompareScreen(container = container, onHome = goHome)
        }

        composable(Routes.NUMBERS) {
            NumbersScreen(container = container, onHome = goHome)
        }

        composable(Routes.GAMES) {
            GamesScreen(
                onMatch = { navController.navigate(Routes.MATCH) },
                onTapNumber = { navController.navigate(Routes.TAP_NUMBER) },
                onPattern = { navController.navigate(Routes.PATTERN) },
                onAddition = { navController.navigate(Routes.ADDITION) },
                onShapes = { navController.navigate(Routes.SHAPES) },
                onMissing = { navController.navigate(Routes.MISSING) },
                onBack = { navController.popBackStack() },
                onHome = goHome,
            )
        }

        composable(Routes.MATCH) {
            MatchScreen(container = container, onHome = goHome)
        }

        composable(Routes.TAP_NUMBER) {
            TapNumberScreen(container = container, onHome = goHome)
        }

        composable(Routes.PATTERN) {
            PatternScreen(container = container, onHome = goHome)
        }

        composable(Routes.ADDITION) {
            AdditionScreen(container = container, onHome = goHome)
        }

        composable(Routes.SHAPES) {
            ShapesScreen(container = container, onHome = goHome)
        }

        composable(Routes.MISSING) {
            MissingScreen(container = container, onHome = goHome)
        }

        composable(Routes.GARDEN) {
            GardenScreen(container = container, onHome = goHome)
        }

        composable(Routes.GATE) {
            GrownUpsGateScreen(
                onPass = {
                    navController.navigate(Routes.SETTINGS) {
                        popUpTo(Routes.GATE) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(container = container, onHome = goHome)
        }
    }
}
