package `in`.reelbreak.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent
import `in`.reelbreak.app.utils.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScrollTrackerService : AccessibilityService() {

    private lateinit var prefs: SharedPreferences
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val defaultDailyLimit = 100

    override fun onServiceConnected() {
        super.onServiceConnected()
        NotificationHelper.createChannel(this)
        prefs = getSharedPreferences("reelbreak_data", Context.MODE_PRIVATE)
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                val pkg = event.packageName?.toString() ?: return
                if (isTrackedApp(pkg)) {
                    resetIfNewDay()
                    incrementCount()
                    checkLimit()
                }
            }
        }
    }

    private fun incrementCount() {
        val today = dateFormat.format(Date())
        val current = prefs.getInt("count_$today", 0)
        prefs.edit().putInt("count_$today", current + 1).apply()
    }

    private fun resetIfNewDay() {
        val today = dateFormat.format(Date())
        val lastDate = prefs.getString("last_date", "")
        if (lastDate != today) {
            prefs.edit()
                .putString("last_date", today)
                .apply()
        }
    }

    private fun checkLimit() {
        val today = dateFormat.format(Date())
        val count = prefs.getInt("count_$today", 0)
        val limit = prefs.getInt("daily_limit", defaultDailyLimit)
        when {
            count == (limit * 0.8).toInt() -> {
                NotificationHelper.showWarning(
                    this,
                    "⚠️ 80% limit reached! $count/$limit reels today."
                )
            }
            count >= limit -> {
                NotificationHelper.showBlock(
                    this,
                    "🚫 Daily limit hit! $limit reels done. Take a break!"
                )
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
}