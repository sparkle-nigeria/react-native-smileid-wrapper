import UIKit
import React
import SwiftUI
import SmileID

@objc public class SmartSelfieAuthenticationViewProvider: UIView {
  private var hostingController: UIHostingController<SmartSelfieAuthenticationRootView>?

  @objc public var onSuccess: ((NSString) -> Void)?
  @objc public var onError: ((NSString) -> Void)?

  // Props from JS
  @objc public var userId: NSString = ""
  @objc public var jobId: NSString = ""
  @objc public var allowAgentMode: Bool = false
  @objc public var showAttribution: Bool = true
  @objc public var showInstructions: Bool = true
  @objc public var skipApiSubmission: Bool = false
  @objc public var extraPartnerParams: NSString = "" // JSON string

  public override func layoutSubviews() {
    super.layoutSubviews()
    setupView()
  }

  private func setupView() {
    if self.hostingController != nil { return }
    buildView()
  }

  private func parseExtraPartnerParams() -> [String: String] {
    guard let data = (extraPartnerParams as String).data(using: .utf8),
          let json = try? JSONSerialization.jsonObject(with: data) as? [String: String] else {
      return [:]
    }
    return json
  }

  // Called explicitly from ObjC++ after all props are set
  @objc public func buildView() {
    print("📸 AUTH PROVIDER buildView called - userId: \(userId), jobId: \(jobId)")

    guard !userId.isEqual(to: "") else {
      print("📸 AUTH PROVIDER buildView - SKIPPING, userId is empty")
      return
    }

    // Remove existing view if rebuilding
    if let existing = hostingController {
      print("📸 AUTH PROVIDER Removing existing hosting controller")
      existing.view.removeFromSuperview()
      existing.removeFromParent()
      hostingController = nil
    }

    print("📸 AUTH PROVIDER Building SwiftUI view now")

    let rootView = SmartSelfieAuthenticationRootView(
      userId: userId as String,
      jobId: jobId as String,
      allowAgentMode: allowAgentMode,
      showAttribution: showAttribution,
      showInstructions: showInstructions,
      skipApiSubmission: skipApiSubmission,
      extraPartnerParams: parseExtraPartnerParams(),
      onSuccess: { [weak self] json in
        print("📸 AUTH PROVIDER ✅ onSuccess callback fired")
        self?.onSuccess?(json)
      },
      onError: { [weak self] json in
        print("📸 AUTH PROVIDER ❌ onError callback fired: \(json)")
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

    print("📸 AUTH PROVIDER View built successfully")
  }
}