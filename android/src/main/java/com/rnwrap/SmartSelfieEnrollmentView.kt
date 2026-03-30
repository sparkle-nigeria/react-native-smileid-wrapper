package com.rnwrap

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.smileidentity.results.SmartSelfieResult
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

class SmartSelfieEnrollmentView(context: Context) :
  SmileIDComposeHostView(
    context = context,
    shouldUseAndroidLayout = true
  ) {

  var userId by mutableStateOf<String?>(null)
  var jobId by mutableStateOf<String?>(null)
  var allowAgentMode by mutableStateOf(false)
  var showAttribution by mutableStateOf(true)
  var showInstructions by mutableStateOf(true)
  var skipApiSubmission by mutableStateOf(false)
  var extraPartnerParams by mutableStateOf<ImmutableMap<String, String>>(persistentMapOf())

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
      skipApiSubmission = skipApiSubmission,
      extraPartnerParams = extraPartnerParams,
      onResult = { result: SmartSelfieResult ->
        dispatchDirectEvent(eventPropName = "onSuccess", payload = result.toWritableMap())
      },
      onError = { throwable: Throwable ->
        dispatchDirectEvent(eventPropName = "onError", payload = throwable.toSmartSelfieErrorPayload())
      }
    )
  }
}