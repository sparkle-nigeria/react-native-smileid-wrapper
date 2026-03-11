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
import com.smileidentity.compose.SmartSelfieEnrollment
import com.smileidentity.results.SmartSelfieResult
import com.smileidentity.results.SmileIDResult

@Composable
fun SmartSelfieEnrollmentRootView(
  userId: String?,
  jobId: String?,
  allowAgentMode: Boolean,
  showAttribution: Boolean,
  showInstructions: Boolean,
  onResult: (SmartSelfieResult) -> Unit,
  onError: (Throwable) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    if (userId.isNullOrEmpty() || jobId.isNullOrEmpty()) {
      Log.e("SmileID_Native", "⚠️ ROOT VIEW WAITING: userId=$userId, jobId=$jobId")
    } else {
      Log.e("SmileID_Native", "🚀 STARTING ENROLLMENT: userId='$userId', jobId='$jobId'")

      key(userId, jobId) {
        SmileID.SmartSelfieEnrollment(
          userId = userId,
          jobId = jobId,
          allowAgentMode = allowAgentMode,
          showAttribution = showAttribution,
          showInstructions = showInstructions
        ) { result ->
          when (result) {
            // Fix: Use <SmartSelfieResult> to handle generics correctly
            is SmileIDResult.Success<SmartSelfieResult> -> {
              Log.e("SmileID_Native", "✅ SmartSelfie Success! File: ${result.data.selfieFile.absolutePath}")
              onResult(result.data)
            }
            is SmileIDResult.Error -> {
               Log.e("SmileID_Native", "❌ SmartSelfie Error: ${result.throwable.message}")
               onError(result.throwable)
            }
          }
        }
      }
    }
  }
}
