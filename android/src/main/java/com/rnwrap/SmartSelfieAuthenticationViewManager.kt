package com.rnwrap

import android.util.Log
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.SmartSelfieAuthenticationViewManagerDelegate
import com.facebook.react.viewmanagers.SmartSelfieAuthenticationViewManagerInterface
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import org.json.JSONObject

@ReactModule(name = SmartSelfieAuthenticationViewManager.REACT_CLASS)
class SmartSelfieAuthenticationViewManager : SimpleViewManager<SmartSelfieAuthenticationView>(),
    SmartSelfieAuthenticationViewManagerInterface<SmartSelfieAuthenticationView> {

  private val delegate: ViewManagerDelegate<SmartSelfieAuthenticationView> =
      SmartSelfieAuthenticationViewManagerDelegate(this)

  override fun getDelegate(): ViewManagerDelegate<SmartSelfieAuthenticationView> = delegate

  override fun getName(): String = REACT_CLASS

  override fun createViewInstance(context: ThemedReactContext): SmartSelfieAuthenticationView {
    return SmartSelfieAuthenticationView(context)
  }

  @ReactProp(name = "userId")
  override fun setUserId(view: SmartSelfieAuthenticationView, value: String?) {
    Log.d("SmileID_Native", "🔵 Auth setUserId called with: '$value'")
    view.userId = value
  }

  @ReactProp(name = "jobId")
  override fun setJobId(view: SmartSelfieAuthenticationView, value: String?) {
    Log.d("SmileID_Native", "🔵 Auth setJobId called with: '$value'")
    view.jobId = value
  }

  @ReactProp(name = "allowNewEnroll")
  override fun setAllowNewEnroll(view: SmartSelfieAuthenticationView, value: Boolean) {
    view.allowNewEnroll = value
  }

  @ReactProp(name = "allowAgentMode")
  override fun setAllowAgentMode(view: SmartSelfieAuthenticationView, value: Boolean) {
    view.allowAgentMode = value
  }

  @ReactProp(name = "showAttribution")
  override fun setShowAttribution(view: SmartSelfieAuthenticationView, value: Boolean) {
    view.showAttribution = value
  }

  @ReactProp(name = "showInstructions")
  override fun setShowInstructions(view: SmartSelfieAuthenticationView, value: Boolean) {
    view.showInstructions = value
  }

  @ReactProp(name = "skipApiSubmission")
  override fun setSkipApiSubmission(view: SmartSelfieAuthenticationView, value: Boolean) {
    view.skipApiSubmission = value
  }

  @ReactProp(name = "extraPartnerParams")
  override fun setExtraPartnerParams(view: SmartSelfieAuthenticationView, value: String?) {
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

  @ReactProp(name = "smileSensitivity")
  override fun setSmileSensitivity(view: SmartSelfieAuthenticationView, value: String?) {
    view.smileSensitivity = value
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any> {
    return mutableMapOf(
        "topSuccess" to mutableMapOf("registrationName" to "onSuccess"),
        "topError" to mutableMapOf("registrationName" to "onError")
    )
  }

  companion object {
    const val REACT_CLASS = "SmartSelfieAuthenticationView"
  }
}