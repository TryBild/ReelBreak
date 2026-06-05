package `in`.reelbreak.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(onDone: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0C18)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "set up your profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEEEAF8)
            )
            Text(
                text = "this is how we'll know you",
                fontSize = 14.sp,
                color = Color(0xFF6B6880)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("your name", color = Color(0xFF6B6880)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFA78BFA),
                    unfocusedBorderColor = Color(0xFF2A2838),
                    focusedTextColor = Color(0xFFEEEAF8),
                    unfocusedTextColor = Color(0xFFEEEAF8),
                    cursorColor = Color(0xFFA78BFA)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it.lowercase().replace(" ", "") },
                label = { Text("username", color = Color(0xFF6B6880)) },
                singleLine = true,
                prefix = { Text("@", color = Color(0xFFA78BFA)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFA78BFA),
                    unfocusedBorderColor = Color(0xFF2A2838),
                    focusedTextColor = Color(0xFFEEEAF8),
                    unfocusedTextColor = Color(0xFFEEEAF8),
                    cursorColor = Color(0xFFA78BFA)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (error.isNotEmpty()) {
                Text(text = error, color = Color.Red, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    when {
                        name.isBlank() -> error = "name nahi daala"
                        username.isBlank() -> error = "username nahi daala"
                        username.length < 3 -> error = "username kam se kam 3 characters"
                        else -> onDone(name)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D5CC9)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "let's go →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}