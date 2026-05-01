package com.example.paryavaran_kavalu.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object NewReport : Screen("new_report")
    object Map : Screen("map")
    object ReportList : Screen("report_list")
    object ReportDetail : Screen("report_detail/{reportId}") {
        fun createRoute(reportId: Long) = "report_detail/$reportId"
    }
}
