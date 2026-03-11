package com.rnwrap

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AbstractComposeView
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.smileidentity.results.SmartSelfieResult

class SmartSelfieEnrollmentView(context: Context) : AbstractComposeView(context) {

  var userId by mutableStateOf<String?>(null)
  var jobId by mutableStateOf<String?>(null)
  var allowAgentMode by mutableStateOf(false)
  var showAttribution by mutableStateOf(true)
  var showInstructions by mutableStateOf(true)

  init {
    Log.e("SmileID_Native", "🟢 SmartSelfieEnrollmentView INITED")
  }

  @Composable
  override fun Content() {
    // ❌ REMOVED RANDOM DEFAULTS. 
    // If props are null, these variables will simply be null.
    Log.d("SmileID_Native", "🎨 Rendering Content. Active userId: '$userId', Active jobId: '$jobId'")

    SmartSelfieEnrollmentRootView(
      userId = userId, // Pass null directly if not set
      jobId = jobId,
      allowAgentMode = allowAgentMode,
      showAttribution = showAttribution,
      showInstructions = showInstructions,
      onResult = { result ->
        val params = Arguments.createMap().apply { putString("result", result.selfieFile.absolutePath) }
        sendEvent("topSuccess", params)
      },
      onError = { error ->
        val params = Arguments.createMap().apply { putString("error", error.message) }
        sendEvent("topError", params)
      }
    )
  }

  private fun sendEvent(eventName: String, params: WritableMap) {
    val reactContext = context as? ReactContext
    reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(id, eventName, params)
  }
}
