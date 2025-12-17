package cc.inoki.screenmaster.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cc.inoki.screenmaster.ui.screen.AppListScreen
import cc.inoki.screenmaster.ui.screen.ScreenListScreen

sealed class Screen(val route: String) {
    data object ScreenList : Screen("screen_list")
    data object AppList : Screen("app_list/{displayId}") {
        fun createRoute(displayId: Int) = "app_list/$displayId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ScreenList.route
    ) {
        composable(Screen.ScreenList.route) {
            ScreenListScreen(
                onScreenSelected = { displayId ->
                    navController.navigate(Screen.AppList.createRoute(displayId))
                }
            )
        }

        composable(
            route = Screen.AppList.route,
            arguments = listOf(
                navArgument("displayId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val displayId = backStackEntry.arguments?.getInt("displayId") ?: 0
            AppListScreen(
                displayId = displayId,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}
