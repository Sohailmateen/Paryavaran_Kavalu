package com.example.paryavaran_kavalu.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.paryavaran_kavalu.ui.screens.home.HomeScreen
import com.example.paryavaran_kavalu.ui.screens.splash.SplashScreen
import com.example.paryavaran_kavalu.ui.screens.report.NewReportScreen
import com.example.paryavaran_kavalu.ui.screens.map.MapScreen
import com.example.paryavaran_kavalu.ui.screens.list.ReportListScreen
import com.example.paryavaran_kavalu.ui.screens.detail.ReportDetailScreen
import com.example.paryavaran_kavalu.viewmodel.ReportViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    // Correctly initialize ViewModel using the Factory
    val viewModel: ReportViewModel = viewModel(factory = ReportViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToReport = { navController.navigate(Routes.REPORT) },
                onNavigateToMap = { navController.navigate(Routes.MAP) },
                onNavigateToList = { navController.navigate(Routes.LIST) },
                onNavigateToDetail = { reportId ->
                    navController.navigate(Routes.detailRoute(reportId))
                }
            )
        }

        composable(
            route = Routes.REPORT,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; nullable = true },
                navArgument("lng") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull()
            NewReportScreen(
                viewModel = viewModel,
                lat = lat,
                lng = lng,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                viewModel = viewModel,
                onNavigateToReport = { lat, lng -> 
                    navController.navigate(Routes.reportRoute(lat, lng))
                },
                onBack = { navController.popBackStack() },
                onMarkerClick = { reportId ->
                    navController.navigate(Routes.detailRoute(reportId))
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ReportDetailScreen(
                reportId = reportId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LIST) {
            ReportListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { reportId ->
                    navController.navigate(Routes.detailRoute(reportId))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
