package com.example.paryavaran_kavalu.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val REPORT = "report"
    const val MAP = "map"
    const val DETAIL = "detail/{reportId}"
    const val LIST = "list"

    fun detailRoute(reportId: String) = "detail/$reportId"
}
