import UIKit
import React
import SwiftUI
import SmileID

@objc public class SmartSelfieEnrollmentViewProvider: UIView {
  private var hostingController: UIHostingController<SmartSelfieEnrollmentRootView>?

  @objc public var onSuccess: ((NSString) -> Void)?
  @objc public var onError: ((NSString) -> Void)?

  // Props from JS
  @objc public var userId: NSString = ""
  @objc public var jobId: NSString = ""
  @objc public var allowAgentMode: Bool = false
  @objc public var showAttribution: Bool = true
  @objc public var showInstructions: Bool = true
  @objc public var skipApiSubmission: Bool = false
  @objc public var extraPartnerParams: NSString = ""

  private func parseExtraPartnerParams() -> [String: String] {
    guard let data = (extraPartnerParams as String).data(using: .utf8),
          let json = try? JSONSerialization.jsonObject(with: data) as? [String: String] else {
      return [:]
    }
    return json
  }

  @objc public func buildView() {
    NSLog("📸 [SmileID Provider] buildView called - userId: %@, jobId: %@", userId, jobId)

    guard !userId.isEqual(to: "") else {
      NSLog("📸 [SmileID Provider] buildView - skipping, userId is empty")
      return
    }

    if let existing = hostingController {
      NSLog("📸 [SmileID Provider] Removing existing hosting controller")
      existing.view.removeFromSuperview()
      existing.removeFromParent()
      hostingController = nil
    }

    let rootView = SmartSelfieEnrollmentRootView(
      userId: userId as String,
      jobId: jobId as String,
      allowAgentMode: allowAgentMode,
      showAttribution: showAttribution,
      showInstructions: showInstructions,
      skipApiSubmission: skipApiSubmission,
      extraPartnerParams: parseExtraPartnerParams(),
      onSuccess: { [weak self] json in
        NSLog("📸 [SmileID Provider] ✅ onSuccess callback fired")
        self?.onSuccess?(json)
      },
      onError: { [weak self] json in
        NSLog("📸 [SmileID Provider] ❌ onError callback fired")
        self?.onError?(json)
      }
    )

    let hc = UIHostingController(rootView: rootView)
    self.hostingController = hc
    addSubview(hc.view)
    hc.view.translatesAutoresizingMaskIntoConstraints = false
    NSLayoutConstraint.activate([
      hc.view.topAnchor.constraint(equalTo: topAnchor),
      hc.view.bottomAnchor.constraint(equalTo: bottomAnchor),
      hc.view.leadingAnchor.constraint(equalTo: leadingAnchor),
      hc.view.trailingAnchor.constraint(equalTo: trailingAnchor),
    ])
    hc.view.overrideUserInterfaceStyle = .light

    if let parentVC = self.reactViewController() {
      parentVC.addChild(hc)
      hc.didMove(toParent: parentVC)
    }
  }
}