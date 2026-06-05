package `in`.reelbreak.app

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "361999808328-o97io4dr8mh5p2vj2dhsjah6kplipn0e.apps.googleusercontent.com"

@Composable
fun LoginScreen(activity: Activity, onLoginSuccess: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0C18)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "🧠",
                fontSize = 64.sp
            )
            Text(
                text = "ReelBreak",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEEEAF8)
            )
            Text(
                text = "take back your brain",
                fontSize = 14.sp,
                color = Color(0xFF6B6880)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMsg.isNotEmpty()) {
                Text(text = errorMsg, color = Color.Red, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMsg = ""
                        try {
                            val credentialManager = CredentialManager.create(activity)
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(WEB_CLIENT_ID)
                                .build()
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()
                            val result = credentialManager.getCredential(
                                request = request,
                                context = activity
                            )
                            val credential = result.credential
                            if (credential is PublicKeyCredential) {
                                onLoginSuccess()
                            } else {
                                GoogleIdTokenCredential.createFrom(credential.data)
                                onLoginSuccess()
                            }
                        } catch (e: GetCredentialException) {
                            errorMsg = "Sign-in failed. Try again."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D5CC9)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Continue with Google",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}