package com.kev.lydia.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

// ------------------------- TopBar -------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = { Text("Contacts", fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = { /* TODO: Search action */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}