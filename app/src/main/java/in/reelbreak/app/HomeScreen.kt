package `in`.reelbreak.app

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import kotlin.math.min

@Composable
fun HomeScreen(userName: String) {
    val reelsToday = remember { mutableStateOf(34) }
    val dailyLimit = remember { mutableStateOf(100) }
    val streak = remember { mutableStateOf(3) }

    val brainHealth = ((1f - reelsToday.value.toFloat() / dailyLimit.value.toFloat()) * 100f)
        .coerceIn(0f, 100f)

    val animatedProgress = animateFloatAsState(
        targetValue = brainHealth / 100f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "brain"
    )

    val vibeMsg = when {
        brainHealth >= 80 -> "you're crushing it today 💜"
        brainHealth >= 60 -> "brain mostly charged. keep going 💜"
        brainHealth >= 40 -> "getting close to your limit 🌿"
        brainHealth >= 20 -> "almost at limit bhai 😬"
        else -> "limit hit! go drink water 🧠"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0C18))
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Greeting
        Text(
            text = "good morning",
            fontSize = 12.sp,
            color = Color(0xFF6B6880),
            letterSpacing = 1.sp
        )
        Text(
            text = "$userName ✌️",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEEEAF8),
            modifier = Modifier.padding(bottom = 28.dp)
        )

        // Brain Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 8.dp)
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                val sweepAngle = animatedProgress.value * 360f
                drawArc(
                    color = Color(0xFF1F1D31),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = stroke
                )
                drawArc(
                    color = Color(0xFFA78BFA),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = stroke
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🧠", fontSize = 36.sp)
                Text(
                    text = "${brainHealth.toInt()}%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA78BFA)
                )
                Text(
                    text = "brain health",
                    fontSize = 10.sp,
                    color = Color(0xFF6B6880)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Vibe message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1C1A2E))
                .padding(14.dp)
        ) {
            Text(
                text = vibeMsg,
                fontSize = 13.sp,
                color = Color(0xFFC4B5FD),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                value = "${reelsToday.value}",
                label = "reels today",
                valueColor = Color(0xFFA78BFA),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "${dailyLimit.value}",
                label = "daily limit",
                valueColor = Color(0xFF67E8F9),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Streak
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1C1A2E))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "🔥", fontSize = 22.sp)
            Column {
                Text(
                    text = "${streak.value} day streak",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFEEEAF8)
                )
                Text(
                    text = "stayed under limit",
                    fontSize = 11.sp,
                    color = Color(0xFF6B6880)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar
        val barPct = (reelsToday.value.toFloat() / dailyLimit.value.toFloat()).coerceIn(0f, 1f)
        val barColor = if (barPct > 0.8f) Color(0xFFF87171) else Color(0xFFA78BFA)
        val animatedBar = animateFloatAsState(
            targetValue = barPct,
            animationSpec = tween(800),
            label = "bar"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("today's usage", fontSize = 11.sp, color = Color(0xFF6B6880))
            Text("${reelsToday.value} / ${dailyLimit.value}", fontSize = 11.sp, color = Color(0xFF6B6880))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF1F1D31))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedBar.value)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1A2E))
            .padding(14.dp)
    ) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF6B6880)
        )
    }
}