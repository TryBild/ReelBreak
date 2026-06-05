package `in`.reelbreak.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(userName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0C18)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "🧠", fontSize = 64.sp)
            Text(
                text = "good morning",
                fontSize = 13.sp,
                color = Color(0xFF6B6880)
            )
            Text(
                text = "$userName ✌️",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEEEAF8)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "dashboard coming soon",
                fontSize = 14.sp,
                color = Color(0xFF6B6880)
            )
        }
    }
}