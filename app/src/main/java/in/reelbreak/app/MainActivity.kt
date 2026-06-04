package `in`.reelbreak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.reelbreak.app.ui.theme.ReelBreakTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReelBreakTheme {
                ReelBreakScreen()
            }
        }
    }
}

@Composable
fun ReelBreakScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("reelbreak_data", android.content.Context.MODE_PRIVATE)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    var scrollCount by remember {
        mutableIntStateOf(prefs.getInt("count_$today", 0))
    }
    val limit = prefs.getInt("daily_limit", 100)
    val progress = (scrollCount.toFloat() / limit).coerceIn(0f, 1f)

    // Brain health — 100% healthy to 0%
    val brainHealth = ((1f - progress) * 100).toInt()

    // Brain color — green to red
    val brainColor = when {
        progress < 0.5f -> Color(0xFF56D364)
        progress < 0.8f -> Color(0xFFE3B341)
        else -> Color(0xFFF85149)
    }

    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Header
            Text(
                text = "ReelBreak",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF56D364)
            )
            Text(
                text = "Break the Reel. Reclaim Your Mind.",
                fontSize = 12.sp,
                color = Color(0xFF8B949E)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Brain Visual
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    // Background circle
                    drawArc(
                        color = Color(0xFF21262D),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                    // Progress arc
                    drawArc(
                        color = brainColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🧠",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "$brainHealth%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = brainColor
                    )
                    Text(
                        text = "Brain Health",
                        fontSize = 11.sp,
                        color = Color(0xFF8B949E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today's count
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$scrollCount",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = brainColor
                        )
                        Text(
                            text = "Reels Today",
                            fontSize = 11.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }

                // Limit
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$limit",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF58A6FF)
                        )
                        Text(
                            text = "Daily Limit",
                            fontSize = 11.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Today's Progress",
                            fontSize = 12.sp,
                            color = Color(0xFF8B949E)
                        )
                        Text(
                            text = "$scrollCount / $limit",
                            fontSize = 12.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = brainColor,
                        trackColor = Color(0xFF21262D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Message
            val statusMsg = when {
                progress == 0f -> "You're doing great! No reels yet today 🎉"
                progress < 0.5f -> "Good control! Keep it up 💪"
                progress < 0.8f -> "Slow down, you're halfway there ⚠️"
                progress < 1f -> "Almost at limit! Stop scrolling 🚨"
                else -> "Limit hit! Take a break now 🚫"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        progress >= 1f -> Color(0xFF3D1A1A)
                        progress >= 0.8f -> Color(0xFF3D2E0A)
                        else -> Color(0xFF161B22)
                    }
                )
            ) {
                Text(
                    text = statusMsg,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = Color(0xFFC9D1D9)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Refresh Button
            Button(
                onClick = {
                    scrollCount = prefs.getInt("count_$today", 0)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF238636)
                )
            ) {
                Text(
                    text = "Refresh Count",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}