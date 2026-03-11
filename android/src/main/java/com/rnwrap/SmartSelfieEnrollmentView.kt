package com.rnwrap

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
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
    Log.d("SmileID_Native", "Rendering. Active userId: '$userId', Active jobId: '$jobId'")

    SmartSelfieEnrollmentRootView(
      userId = userId,
      jobId = jobId,
      allowAgentMode = allowAgentMode,
      showAttribution = showAttribution,
      showInstructions = showInstructions,
      onResult = { result: SmartSelfieResult ->
        dispatchDirectEvent(eventPropName = "onSuccess", payload = result.toWritableMap())
      },
      onError = { throwable: Throwable ->
        dispatchDirectEvent(eventPropName = "onError", payload = throwable.toSmartSelfieErrorPayload())
      }
    )
  }

  private fun dispatchDirectEvent(eventPropName: String, payload: WritableMap) {
    val eventName = when (eventPropName) {
      "onSuccess" -> "topSuccess"
      "onError" -> "topError"
      else -> eventPropName
    }
    val reactContext = context as? ReactContext
    reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(id, eventName, payload)
  }
}
