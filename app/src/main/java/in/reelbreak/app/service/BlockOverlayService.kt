package `in`.reelbreak.app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

class BlockOverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private var overlayView: ComposeView? = null
    private lateinit var windowManager: WindowManager

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        showBlock()
    }

    private fun showBlock() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
        }

        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@BlockOverlayService)
            setViewTreeSavedStateRegistryOwner(this@BlockOverlayService)
            setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xF00D0C18)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(text = "🧠", fontSize = 64.sp)
                        Text(
                            text = "limit hit!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEEEAF8)
                        )
                        Text(
                            text = "you've used all your reels for today.\ngo touch some grass 🌿",
                            fontSize = 14.sp,
                            color = Color(0xFF6B6880),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF6D5CC9),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "come back tomorrow",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager.removeView(it) }
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, BlockOverlayService::class.java))
        }
        fun stop(context: Context) {
            context.stopService(Intent(context, BlockOverlayService::class.java))
        }
    }
}