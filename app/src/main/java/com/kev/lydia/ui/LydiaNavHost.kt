package com.kev.lydia.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kev.domain.model.Contact
import com.kev.lydia.ui.details.screen.ContactDetailPager
import com.kev.lydia.ui.list.screen.HomeRoute
import com.kev.lydia.ui.list.screen.HomeViewModel
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
            // The list screen's ViewModel holds the contacts that were listed when one was tapped.
            val homeEntry = remember(backStackEntry) { navController.getBackStackEntry(LydiaRoutes.HOME) }
            val contacts: List<Contact> = hiltViewModel<HomeViewModel>(homeEntry).openedContacts
            if (contacts.isEmpty()) {
                // Nothing to show (e.g. the process was recreated on this screen): back to the list.
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                ContactDetailPager(
                    contacts = contacts,
                    startIndex = startIndex.coerceIn(0, contacts.lastIndex),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

