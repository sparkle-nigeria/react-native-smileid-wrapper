package com.rnwrap

import android.util.Log
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.SmartSelfieEnrollmentViewManagerDelegate
import com.facebook.react.viewmanagers.SmartSelfieEnrollmentViewManagerInterface
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import org.json.JSONObject

@ReactModule(name = SmartSelfieEnrollmentViewManager.REACT_CLASS)
class SmartSelfieEnrollmentViewManager : SimpleViewManager<SmartSelfieEnrollmentView>(),
    SmartSelfieEnrollmentViewManagerInterface<SmartSelfieEnrollmentView> {

  private val delegate: ViewManagerDelegate<SmartSelfieEnrollmentView> =
      SmartSelfieEnrollmentViewManagerDelegate(this)

  override fun getDelegate(): ViewManagerDelegate<SmartSelfieEnrollmentView> = delegate

  override fun getName(): String = REACT_CLASS

  override fun createViewInstance(context: ThemedReactContext): SmartSelfieEnrollmentView {
    return SmartSelfieEnrollmentView(context)
  }

  @ReactProp(name = "userId")
  override fun setUserId(view: SmartSelfieEnrollmentView, value: String?) {
    Log.d("SmileID_Native", "🔵 setUserId called with: '$value'")
    view.userId = value
  }

  @ReactProp(name = "jobId")
  override fun setJobId(view: SmartSelfieEnrollmentView, value: String?) {
    Log.d("SmileID_Native", "🔵 setJobId called with: '$value'")
    view.jobId = value
  }

  @ReactProp(name = "allowAgentMode")
  override fun setAllowAgentMode(view: SmartSelfieEnrollmentView, value: Boolean) {
    view.allowAgentMode = value
  }

  @ReactProp(name = "showAttribution")
  override fun setShowAttribution(view: SmartSelfieEnrollmentView, value: Boolean) {
    view.showAttribution = value
  }

  @ReactProp(name = "showInstructions")
  override fun setShowInstructions(view: SmartSelfieEnrollmentView, value: Boolean) {
    view.showInstructions = value
  }

  @ReactProp(name = "skipApiSubmission")
  override fun setSkipApiSubmission(view: SmartSelfieEnrollmentView, value: Boolean) {
    view.skipApiSubmission = value
  }

  @ReactProp(name = "extraPartnerParams")
  override fun setExtraPartnerParams(view: SmartSelfieEnrollmentView, value: String?) {
    Log.d("SmileID_Native", "🔵 setExtraPartnerParams called with: '$value'")
    if (value != null) {
      try {
        val json = JSONObject(value)
        val map = mutableMapOf<String, String>()
        json.keys().forEach { key -> map[key] = json.getString(key) }
        view.extraPartnerParams = map.toImmutableMap()
      } catch (e: Exception) {
        Log.e("SmileID_Native", "Failed to parse extraPartnerParams: $value", e)
        view.extraPartnerParams = persistentMapOf()
      }
    } else {
      view.extraPartnerParams = persistentMapOf()
    }
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any> {
    return mutableMapOf(
        "topSuccess" to mutableMapOf("registrationName" to "onSuccess"),
        "topError" to mutableMapOf("registrationName" to "onError")
    )
  }

  companion object {
    const val REACT_CLASS = "SmartSelfieEnrollmentView"
  }
}