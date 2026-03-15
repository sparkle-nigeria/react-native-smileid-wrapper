package com.rnwrap

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smileidentity.SmileID
import com.smileidentity.compose.SmartSelfieAuthentication
import com.smileidentity.results.SmartSelfieResult
import com.smileidentity.results.SmileIDResult
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Composable
fun SmartSelfieAuthenticationRootView(
  userId: String?,
  jobId: String?,
  allowNewEnroll: Boolean,
  allowAgentMode: Boolean,
  showAttribution: Boolean,
  showInstructions: Boolean,
  skipApiSubmission: Boolean,
  extraPartnerParams: ImmutableMap<String, String>,
  smileSensitivity: String?,
  onResult: (SmartSelfieResult) -> Unit,
  onError: (Throwable) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    if (userId.isNullOrEmpty() || jobId.isNullOrEmpty()) {
      Log.e("SmileID_Native", "⚠️ AUTH ROOT VIEW WAITING: userId=$userId, jobId=$jobId")
    } else {
      Log.e("SmileID_Native", "🚀 STARTING AUTHENTICATION: userId='$userId', jobId='$jobId'")

      key(userId, jobId) {
        SmileID.SmartSelfieAuthentication(
          userId = userId,
          jobId = jobId,
          allowNewEnroll = allowNewEnroll,
          allowAgentMode = allowAgentMode,
          showAttribution = showAttribution,
          showInstructions = showInstructions,
          skipApiSubmission = skipApiSubmission,
          extraPartnerParams = extraPartnerParams,
        ) { result ->
          when (result) {
            is SmileIDResult.Success<SmartSelfieResult> -> {
              Log.e("SmileID_Native", "✅ SmartSelfie Auth Success! File: ${result.data.selfieFile.absolutePath}")
              onResult(result.data)
            }
            is SmileIDResult.Error -> {
              Log.e("SmileID_Native", "❌ SmartSelfie Auth Error: ${result.throwable.message}")
              onError(result.throwable)
            }
          }
        }
      }
    }
  }
}