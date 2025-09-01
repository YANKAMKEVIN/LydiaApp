package com.kev.lydia.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kev.domain.model.Contact
import com.kev.lydia.ui.details.screen.ContactDetailPager
import com.kev.lydia.ui.list.screen.HomeRoute
import com.kev.lydia.ui.navigation.LydiaRoutes

@Composable
fun LydiaNavHost(modifier: Modifier = Modifier, isOffline: State<Boolean>) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = LydiaRoutes.HOME,
    ) {
        composable(route = LydiaRoutes.HOME) {
            HomeRoute(
                navController = navController,
                isOffline = isOffline
            )
        }

        composable(
            route = LydiaRoutes.DETAIL_ROUTE,
            arguments = listOf(navArgument(LydiaRoutes.DETAIL_ARG) { type = NavType.IntType })
        ) { backStackEntry ->
            val startIndex = backStackEntry.arguments?.getInt(LydiaRoutes.DETAIL_ARG) ?: 0
            val contacts: List<Contact> =
                navController.previousBackStackEntry?.savedStateHandle?.get("contacts")
                    ?: emptyList()
            ContactDetailPager(
                contacts = contacts,
                startIndex = startIndex,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

