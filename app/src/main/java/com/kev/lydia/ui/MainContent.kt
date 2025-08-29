package com.kev.lydia.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kev.domain.model.Contact

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeRoute(
            /*onContactClick = { contact: Contact ->
                // TODO: Navigation vers l'écran détail du contact
            }*/
        )
    }
}