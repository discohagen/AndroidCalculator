package com.example.myapplication.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.ThemeViewModel

@Composable
fun ToggleThemeButton(
    viewModel: ThemeViewModel = viewModel()
) {
    Button(
        modifier = Modifier
            .padding(5.dp)
            .width(150.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        onClick = viewModel::onChange
    ) {
        val text = if (viewModel.isDark()) {
            "Light Mode"
        } else {
            "Dark Mode"
        }
        Text(color = MaterialTheme.colorScheme.primary, text = text)
    }
}