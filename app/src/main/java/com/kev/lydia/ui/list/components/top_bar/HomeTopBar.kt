package com.kev.lydia.ui.list.components.top_bar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kev.lydia.R
import com.kev.lydia.ui.theme.LydiaTheme

@Composable
fun HomeTopBar(
    searchQuery: String = "",
    isSearching: Boolean = false,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Surface(
        tonalElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = if (isSearching) 4.dp else 16.dp, end = 16.dp)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearching) {
                IconButton(onClick = onSearchToggle) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = context.getString(R.string.list_top_bar_close_search)
                    )
                }
            }
            Crossfade(
                targetState = isSearching,
                label = context.getString(R.string.list_top_bar_cross_fade_label),
                modifier = Modifier.weight(1f)
            ) { searching ->
                if (searching) {
                    // Opening the search should let the user type right away.
                    val focusRequester = remember { FocusRequester() }
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text(context.getString(R.string.list_top_bar_search_placeholder)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        // Empties the field without leaving the search.
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = context.getString(R.string.list_top_bar_clear_search)
                                    )
                                }
                            }
                        },
                        // The list filters while typing: the keyboard action only closes the keyboard.
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )
                } else {
                    Text(
                        text = context.getString(R.string.list_top_bar_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            if (!isSearching) {
                IconButton(onClick = onSearchToggle) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = context.getString(R.string.list_top_bar_search_title)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    LydiaTheme {
        HomeTopBar(
            searchQuery = "Jane Doe",
            onSearchQueryChange = {},
            onSearchToggle = {}
        )
    }
}
