package `in`.reelbreak.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent

class ScrollTrackerService : AccessibilityService() {

    private var scrollCount = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                val pkg = event.packageName?.toString() ?: return
                if (isTrackedApp(pkg)) {
                    scrollCount++
                    // TODO: Save to local DB + check limit
                }
            }
        }
    }

    private fun isTrackedApp(pkg: String): Boolean {
        return pkg in listOf(
            "com.instagram.android",
            "com.google.android.youtube",
            "com.snapchat.android",
            "com.facebook.katana"
        )
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }
        serviceInfo = info
    }
}