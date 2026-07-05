package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.ProfileViewModel

@Composable
fun ProfileSelectionScreen(
    viewModel: ProfileViewModel,
    onProfileSelected: (Int) -> Unit
) {
    val users by viewModel.allUsers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Кто за рулем?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        users.forEach { user ->
            Button(
                onClick = { 
                    viewModel.selectUser(user.id)
                    onProfileSelected(user.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Крупная кнопка по ТЗ
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = user.name,
                    fontSize = 20.sp
                )
            }
        }

        if (users.isEmpty()) {
            CircularProgressIndicator()
            Text("Загрузка профилей...")
        }
    }
}
