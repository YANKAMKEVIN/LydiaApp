package com.kev.lydia.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kev.lydia.ui.theme.LydiaTheme

@Composable
fun HomeTopBar(
    userAvatarUrl: String,
    searchQuery: String = "",
    isSearching: Boolean = false,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onFilterClick: () -> Unit,
    isFilterActive: Boolean = true
) {
    Surface(
        tonalElevation = 8.dp,
        //color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Crossfade(targetState = isSearching, label = "SearchCrossfade") { searching ->
                if (searching) {
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search contacts") },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            cursorColor = Color.Black,
                        )
                    )
                } else {
                    Text(
                        text = "Contacts",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onSearchToggle) {
                Icon(
                    imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (isSearching) "Close Search" else "Search"
                )
            }
            //TODO("Add filter icon")
            /*Box {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.Filter,
                        contentDescription = "Filter"
                    )
                }

                if (isFilterActive) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }*/
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    LydiaTheme {
        HomeTopBar(
            userAvatarUrl = "https://randomuser.me/api/portraits/men/1.jpg",
            searchQuery = "Jane Doe",
            onSearchQueryChange = {},
            onFilterClick = {},
            isFilterActive = true,
            onSearchToggle = {}
        )
    }
}
