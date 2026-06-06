package `in`.reelbreak.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import `in`.reelbreak.app.service.BlockOverlayService
import `in`.reelbreak.app.WarningActivity

class ScrollTrackerService : AccessibilityService(), LifecycleOwner, SavedStateRegistryOwner {

    private var scrollCount = 0
    private var lastScrollY = 0
    private var scrollRunnable: Runnable? = null

    private val SCROLL_END_DELAY_MS = 500L
    private val MIN_REEL_SCROLL_PX = 300

    private var overlayView: ComposeView? = null
    private lateinit var windowManager: WindowManager
    private val handler = Handler(Looper.getMainLooper())

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onServiceConnected() {
        super.onServiceConnected()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
            packageNames = arrayOf(
                "com.instagram.android",
                "com.google.android.youtube",
                "com.snapchat.android",
                "com.facebook.katana"
            )
        }
        serviceInfo = info
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return

        val currentScrollY = event.scrollY

        scrollRunnable?.let { handler.removeCallbacks(it) }

        scrollRunnable = Runnable {
            val scrollDiff = currentScrollY - lastScrollY

            when {
                scrollDiff >= MIN_REEL_SCROLL_PX -> {
                    lastScrollY = currentScrollY
                    scrollCount++
                    showOrUpdateOverlay()

                    // Har 50 pe warning popup
                    if (scrollCount % 50 == 0) {
                        WarningActivity.launch(this, scrollCount)
                    }

                    // Daily limit pe hard block
                    if (scrollCount >= DAILY_LIMIT) {
                        BlockOverlayService.start(this)
                    }
                }
                scrollDiff <= -MIN_REEL_SCROLL_PX -> {
                    // Backward scroll — count nahi, sirf position update
                    lastScrollY = currentScrollY
                }
                // Same reel pe thoda scroll — ignore
            }
        }

        handler.postDelayed(scrollRunnable!!, SCROLL_END_DELAY_MS)
    }

    private fun showOrUpdateOverlay() {
        if (overlayView == null) {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = 60
            }

            overlayView = ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@ScrollTrackerService)
                setViewTreeSavedStateRegistryOwner(this@ScrollTrackerService)
                setContent { OverlayCounter(count = scrollCount) }
            }
            windowManager.addView(overlayView, params)
        } else {
            overlayView?.setContent { OverlayCounter(count = scrollCount) }
        }
    }

    override fun onInterrupt() {
        removeOverlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    private fun removeOverlay() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }

    companion object {
        const val DAILY_LIMIT = 100
    }
}