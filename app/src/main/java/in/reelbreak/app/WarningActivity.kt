package `in`.reelbreak.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.reelbreak.app.ui.theme.ReelBreakTheme

class WarningActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val count = intent.getIntExtra("count", 50)

        setContent {
            ReelBreakTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xDD000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .background(Color(0xFF1C1A2E), RoundedCornerShape(24.dp))
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "🧠", fontSize = 48.sp)

                        Text(
                            text = "$count reels ho gaye!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEEEAF8)
                        )

                        Text(
                            text = "Bhai ruk ja thoda.\nApna time bachaa.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B6880),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        // Stop button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF6D5CC9), RoundedCornerShape(12.dp))
                                .clickable {
                                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                        addCategory(Intent.CATEGORY_HOME)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    startActivity(homeIntent)
                                    finish()
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🛑 Stop — Ghar Ja",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Continue button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2A2838), RoundedCornerShape(12.dp))
                                .clickable { finish() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "▶ Continue",
                                fontSize = 15.sp,
                                color = Color(0xFF6B6880)
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun launch(context: Context, count: Int) {
            val intent = Intent(context, WarningActivity::class.java).apply {
                putExtra("count", count)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}