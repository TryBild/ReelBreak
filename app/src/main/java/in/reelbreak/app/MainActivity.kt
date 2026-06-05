package `in`.reelbreak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import `in`.reelbreak.app.ui.theme.ReelBreakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val activity = this
        setContent {
            ReelBreakTheme {
                var screen by remember { mutableStateOf("login") }
                var userName by remember { mutableStateOf("") }

                when (screen) {
                    "login" -> LoginScreen(
                        activity = activity,
                        onLoginSuccess = { screen = "onboarding" }
                    )
                    "onboarding" -> OnboardingScreen(
                        onDone = { name ->
                            userName = name
                            screen = "home"
                        }
                    )
                    "home" -> HomeScreen(userName = userName)
                }
            }
        }
    }
}