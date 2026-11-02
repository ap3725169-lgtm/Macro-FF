package com.iswipemacro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.iswipemacro.app.ui.HomeScreen
import com.iswipemacro.app.ui.theme.ISwipeMacroTheme
import com.iswipemacro.app.viewmodel.GestureViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GestureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISwipeMacroTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
