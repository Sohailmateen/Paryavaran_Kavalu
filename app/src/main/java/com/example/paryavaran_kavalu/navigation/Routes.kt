package com.example.paryavaran_kavalu.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val REPORT = "report?lat={lat}&lng={lng}"
    const val MAP = "map"
    const val DETAIL = "detail/{reportId}"
    const val LIST = "list"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ROLE_SELECTION = "role_selection"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"

    fun detailRoute(reportId: String) = "detail/$reportId"
    fun reportRoute(lat: Double? = null, lng: Double? = null): String {
        return if (lat != null && lng != null) "report?lat=$lat&lng=$lng" else "report"
    }
}
