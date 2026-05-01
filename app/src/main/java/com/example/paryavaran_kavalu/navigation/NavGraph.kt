package com.example.paryavaran_kavalu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.paryavaran_kavalu.ui.screens.home.HomeScreen
import com.example.paryavaran_kavalu.ui.screens.splash.SplashScreen
import com.example.paryavaran_kavalu.ui.screens.report.NewReportScreen
import com.example.paryavaran_kavalu.ui.screens.map.MapScreen
import com.example.paryavaran_kavalu.ui.screens.list.ReportListScreen
import com.example.paryavaran_kavalu.ui.screens.detail.ReportDetailScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen()
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.NewReport.route) {
            NewReportScreen()
        }
        composable(Screen.Map.route) {
            MapScreen()
        }
        composable(Screen.ReportList.route) {
            ReportListScreen()
        }
        composable(Screen.ReportDetail.route) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")?.toLongOrNull() ?: 0L
            ReportDetailScreen(reportId = reportId)
        }
    }
}
