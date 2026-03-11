package com.rnwrap

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.UiThread
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter

/**
 * Base host for SmileID Compose content inside a React Native Fabric view.
 */
abstract class SmileIDComposeHostView(
  context: Context,
  private val shouldUseAndroidLayout: Boolean = false
) : LinearLayout(context) {

  companion object {
    private const val TAG = "SmileIDComposeHostView"
  }

  init {
    configure(context)
  }

  private var customViewModelStoreOwner: ViewModelStoreOwner? = null
  private var isViewAttached = false

  @Composable
  protected abstract fun Content()

  private fun configure(context: Context) {
    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    val composeView = ComposeView(context).apply {
      layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
      setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
      setupViewModelStoreOwner(this)
      setContent { this@SmileIDComposeHostView.Content() }

      addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
          isViewAttached = true
          Log.d(TAG, "View attached to window")
        }
        override fun onViewDetachedFromWindow(v: View) {
          isViewAttached = false
          Log.d(TAG, "View detached from window")
          // Delay cleanup to allow pending callbacks
          postDelayed({
            if (!isViewAttached) {
              disposeComposition()
              cleanup()
            }
          }, 500)
        }
      })
    }
    addView(composeView)
  }

  private fun setupViewModelStoreOwner(composeView: ComposeView) {
    val owner: ViewModelStoreOwner = when (val ctx = context) {
      is FragmentActivity -> ctx
      else -> object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
      }.also { customViewModelStoreOwner = it }
    }
    composeView.setViewTreeViewModelStoreOwner(owner)
  }

  private fun cleanup() {
    customViewModelStoreOwner?.viewModelStore?.clear()
    customViewModelStoreOwner = null
  }

  /**
   * Get the React view tag for this view.
   * The view's id is set by React Native and serves as the React tag.
   */
  private fun getViewTag(): Int {
    // In React Native, the view's id IS the React tag
    // It's set by the framework when the view is created
    return id
  }

  /**
   * Dispatch a typed direct event to JS using RN's EventDispatcher.
   * Updated to support Fabric architecture with correct event naming and view tag handling.
   */
  protected fun dispatchDirectEvent(
    eventPropName: String,
    payload: WritableMap = Arguments.createMap()
  ) {
    val reactContext = UIManagerHelper.getReactContext(this)
    if (reactContext == null) {
      Log.e(TAG, "ReactContext is null, cannot dispatch event: $eventPropName")
      return
    }

    val viewTag = getViewTag()
    if (viewTag == View.NO_ID || viewTag == 0) {
      Log.e(TAG, "Invalid view tag ($viewTag), cannot dispatch event: $eventPropName")
      return
    }

    Log.d(TAG, "Dispatching event: $eventPropName with viewTag: $viewTag")
    dispatchEventWithTag(reactContext, viewTag, eventPropName, payload)
  }

  /**
   * Safe dispatch that checks if view is still attached
   */
  protected fun dispatchDirectEventSafe(
    eventPropName: String,
    payload: WritableMap = Arguments.createMap()
  ) {
    if (!isViewAttached) {
      Log.w(TAG, "View not attached, storing event for later: $eventPropName")
      // Post to main thread to try again
      post {
        if (isViewAttached) {
          dispatchDirectEvent(eventPropName, payload)
        } else {
          Log.e(TAG, "View still not attached, dropping event: $eventPropName")
        }
      }
      return
    }
    dispatchDirectEvent(eventPropName, payload)
  }

  private fun dispatchEventWithTag(
    reactContext: ReactContext,
    viewTag: Int,
    eventPropName: String,
    payload: WritableMap
  ) {
    try {
      val surfaceId = UIManagerHelper.getSurfaceId(this)
      val dispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, viewTag)

      if (dispatcher == null) {
        Log.e(TAG, "EventDispatcher is null for viewTag: $viewTag, trying fallback")
        // Fallback: try to get dispatcher from surface id
        val fallbackDispatcher = UIManagerHelper.getEventDispatcher(reactContext, surfaceId)
        if (fallbackDispatcher == null) {
          Log.e(TAG, "Fallback dispatcher also null, cannot dispatch: $eventPropName")
          return
        }
        dispatchWithEventDispatcher(fallbackDispatcher, surfaceId, viewTag, eventPropName, payload)
        return
      }

      dispatchWithEventDispatcher(dispatcher, surfaceId, viewTag, eventPropName, payload)
    } catch (e: Exception) {
      Log.e(TAG, "Error dispatching event: $eventPropName", e)
    }
  }

  private fun dispatchWithEventDispatcher(
    dispatcher: com.facebook.react.uimanager.events.EventDispatcher,
    surfaceId: Int,
    viewTag: Int,
    eventPropName: String,
    payload: WritableMap
  ) {
    // Convert prop name to native event name
    // "onSuccess" -> "topSuccess", "onError" -> "topError"
    val nativeEventName = convertToNativeEventName(eventPropName)

    Log.d(TAG, "Dispatching: eventName=$nativeEventName, surfaceId=$surfaceId, viewTag=$viewTag")

    dispatcher.dispatchEvent(
      FabricCompatibleMapEvent(surfaceId, viewTag, nativeEventName, payload)
    )
  }

  /**
   * Convert JS prop name to native event name.
   * React Native expects events prefixed with "top".
   * "onSuccess" -> "topSuccess"
   * "onError" -> "topError"
   */
  private fun convertToNativeEventName(propName: String): String {
    return if (propName.startsWith("on") && propName.length > 2) {
      "top" + propName.substring(2)
    } else {
      propName
    }
  }

  /**
   * Event class that supports both Paper and Fabric architectures.
   * - Paper: uses dispatch(RCTEventEmitter)
   * - Fabric: uses getEventData()
   */
  private class FabricCompatibleMapEvent(
    surfaceId: Int,
    private val viewTag: Int,
    private val eventName: String,
    private val payload: WritableMap
  ) : Event<FabricCompatibleMapEvent>(surfaceId, viewTag) {

    override fun getEventName(): String = eventName

    override fun canCoalesce(): Boolean = false

    // Fabric architecture uses this method to get event data
    override fun getEventData(): WritableMap = payload

    // Paper architecture fallback (deprecated but needed for compatibility)
    @Deprecated("Use getEventData() instead")
    override fun dispatch(rctEventEmitter: RCTEventEmitter) {
      rctEventEmitter.receiveEvent(viewTag, eventName, payload)
    }
  }

  override fun requestLayout() {
    super.requestLayout()
    if (shouldUseAndroidLayout) {
      post { measureAndLayout() }
    }
  }

  @UiThread
  fun measureAndLayout() {
    measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    )
    layout(left, top, right, bottom)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    removeAllViews()
    cleanup()
  }
}