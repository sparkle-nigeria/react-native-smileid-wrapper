package com.rnwrap

import android.util.Log
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.SmartSelfieEnrollmentViewManagerDelegate
import com.facebook.react.viewmanagers.SmartSelfieEnrollmentViewManagerInterface

@ReactModule(name = SmartSelfieEnrollmentViewManager.REACT_CLASS)
class SmartSelfieEnrollmentViewManager : SimpleViewManager<SmartSelfieEnrollmentView>(), 
    SmartSelfieEnrollmentViewManagerInterface<SmartSelfieEnrollmentView> {

  // 🔴 REQUIRED FOR FABRIC (NEW ARCHITECTURE)
  private val delegate: ViewManagerDelegate<SmartSelfieEnrollmentView> = 
      SmartSelfieEnrollmentViewManagerDelegate(this)

  override fun getDelegate(): ViewManagerDelegate<SmartSelfieEnrollmentView> = delegate

  override fun getName(): String = REACT_CLASS

  override fun createViewInstance(context: ThemedReactContext): SmartSelfieEnrollmentView {
    return SmartSelfieEnrollmentView(context)
  }

  // 🔵 Override Interface Methods for Fabric
  // These handle the prop updates from the New Architecture

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
  
  // Stubs for interface method we might not use yet but must implement to satisfy the interface
  override fun setSkipApiSubmission(view: SmartSelfieEnrollmentView, value: Boolean) {
      // Implement if needed
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
