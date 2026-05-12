package com.example.paryavaran_kavalu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.paryavaran_kavalu.ui.screens.profile.ProfileScreen
import com.example.paryavaran_kavalu.ui.screens.profile.edit.EditProfileScreen
import com.example.paryavaran_kavalu.ui.screens.auth.login.LoginScreen
import com.example.paryavaran_kavalu.ui.screens.auth.register.RegisterScreen
import com.example.paryavaran_kavalu.ui.screens.auth.role.RoleSelectionScreen
import com.example.paryavaran_kavalu.viewmodel.ReportViewModel
import com.example.paryavaran_kavalu.viewmodel.AuthViewModel
import com.example.paryavaran_kavalu.viewmodel.AuthState
import com.example.paryavaran_kavalu.viewmodel.UserRole
import com.google.firebase.auth.FirebaseAuth

import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavGraph(navController: NavHostController) {
    val reportViewModel: ReportViewModel = viewModel(factory = ReportViewModel.Factory)
    val authViewModel: AuthViewModel = viewModel()
    
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Server Client ID from google-services.json (type 3 client)
    val webClientId = "459355564850-gs2pk6nm0vhgkug6bpdt0nqnv1qpbcde.apps.googleusercontent.com"

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                // Add ROLE_SELECTION to authRoutes so we can auto-navigate to Home after selecting role
                val authRoutes = listOf(Routes.LOGIN, Routes.REGISTER, Routes.SPLASH, Routes.ROLE_SELECTION, null)
                if (currentRoute in authRoutes || currentRoute?.startsWith("splash") == true) {
                    if (state.role.isNullOrEmpty()) {
                        if (currentRoute != Routes.ROLE_SELECTION) {
                            navController.navigate(Routes.ROLE_SELECTION) {
                                // Clear auth screens from backstack
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    } else {
                        val role = if (state.role == "Volunteer") UserRole.VOLUNTEER else UserRole.CITIZEN
                        reportViewModel.setUserRole(role)
                        navController.navigate(Routes.HOME) {
                            // Using popUpTo(0) ensures the entire backstack (Login/Register/Role) is cleared
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            is AuthState.Error -> {
                if (currentRoute !in listOf(Routes.LOGIN, Routes.REGISTER)) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    if (FirebaseAuth.getInstance().currentUser == null) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        // The LaunchedEffect above will handle Success state
                        // If it's still Idle/Loading, we might need a backup
                        if (authState is AuthState.Idle) {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authState = authState,
                onLoginClick = { email, password ->
                    authViewModel.signInWithEmail(email, password)
                },
                onGoogleCredentialReceived = { credential ->
                    authViewModel.signInWithGoogle(credential)
                },
                onCreateAccountClick = { navController.navigate(Routes.REGISTER) },
                webClientId = webClientId
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authState = authState,
                onRegisterClick = { name, email, password, role ->
                    authViewModel.register(name, email, password, role)
                },
                onBackToLoginClick = { navController.popBackStack() }
            )
        }

        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onContinueClick = { role ->
                    authViewModel.completeRoleSelection(role)
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = reportViewModel,
                onNavigateToReport = { navController.navigate(Routes.REPORT) },
                onNavigateToMap = { navController.navigate(Routes.MAP) },
                onNavigateToList = { navController.navigate(Routes.listRoute(false)) },
                onNavigateToDetail = { reportId ->
                    navController.navigate(Routes.detailRoute(reportId))
                },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = reportViewModel,
                onBack = { navController.popBackStack() },
                onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                onMyReports = { navController.navigate(Routes.listRoute(true)) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EDIT_PROFILE) {
            val user = FirebaseAuth.getInstance().currentUser
            EditProfileScreen(
                currentName = user?.displayName ?: "",
                onBack = { navController.popBackStack() },
                onSave = { name, password, imageUri ->
                    authViewModel.updateProfile(name, password, imageUri)
                    reportViewModel.refreshProfile()
                    navController.popBackStack()
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
                viewModel = reportViewModel,
                lat = lat,
                lng = lng,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                viewModel = reportViewModel,
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
                viewModel = reportViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.LIST,
            arguments = listOf(navArgument("myReports") { type = NavType.BoolType; defaultValue = false })
        ) { backStackEntry ->
            val showOnlyMyReports = backStackEntry.arguments?.getBoolean("myReports") ?: false
            ReportListScreen(
                viewModel = reportViewModel,
                showOnlyMyReports = showOnlyMyReports,
                onNavigateToDetail = { reportId ->
                    navController.navigate(Routes.detailRoute(reportId))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
