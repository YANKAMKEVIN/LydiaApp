package com.kev.lydia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kev.lydia.ui.LydiaNavHost
import com.kev.lydia.ui.MainViewModel
import com.kev.lydia.ui.theme.LydiaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isOffline = viewModel.isOffline.collectAsState()
            LydiaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LydiaNavHost(modifier = Modifier.padding(innerPadding), isOffline = isOffline)
                }
            }
        }
    }
}

