package com.kev.lydia.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kev.domain.model.Contact
import com.kev.lydia.ui.details.screen.ContactDetailPager

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeRoute(
                navController
            )
        }

        composable(
            "detail/{startIndex}",
            arguments = listOf(navArgument("startIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val startIndex = backStackEntry.arguments?.getInt("startIndex") ?: 0
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