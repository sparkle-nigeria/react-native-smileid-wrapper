import SwiftUI
import SmileID

struct SmartSelfieEnrollmentRootView: View, SmartSelfieResultDelegate {
  let userId: String
  let jobId: String
  let allowAgentMode: Bool
  let showAttribution: Bool
  let showInstructions: Bool
  let skipApiSubmission: Bool
  let extraPartnerParams: [String: String]
  let onSuccess: (NSString) -> Void
  let onError: (NSString) -> Void

  init(
    userId: String = "",
    jobId: String = "",
    allowAgentMode: Bool = false,
    showAttribution: Bool = true,
    showInstructions: Bool = true,
    skipApiSubmission: Bool = false,
    extraPartnerParams: [String: String] = [:],
    onSuccess: @escaping (NSString) -> Void = { _ in },
    onError: @escaping (NSString) -> Void = { _ in }
  ) {
    self.userId = userId
    self.jobId = jobId
    self.allowAgentMode = allowAgentMode
    self.showAttribution = showAttribution
    self.showInstructions = showInstructions
    self.skipApiSubmission = skipApiSubmission
    self.extraPartnerParams = extraPartnerParams
    self.onSuccess = onSuccess
    self.onError = onError
    print("📸 [SmileID View] init - userId: \(userId), jobId: \(jobId)")
  }

  var body: some View {
    if userId.isEmpty {
      ProgressView("Loading...")
        .onAppear {
          print("📸 [SmileID View] ⚠️ userId is empty, showing loader")
        }
    } else {
      let _ = print("📸 [SmileID View] Rendering - userId: \(userId), jobId: \(jobId)")

      SmileID.smartSelfieEnrollmentScreen(
        userId: userId,
        jobId: jobId,
        allowAgentMode: allowAgentMode,
        showAttribution: showAttribution,
        showInstructions: showInstructions,
        skipApiSubmission: skipApiSubmission,
        extraPartnerParams: extraPartnerParams,
        delegate: self
      )
    }
  }

  func didSucceed(
    selfieImage: URL,
    livenessImages: [URL],
    apiResponse: SmartSelfieResponse?
  ) {
    print("📸 [SmileID View] ✅ Success - userId: \(userId)")

    var result: [String: Any] = [
      "selfieFile": selfieImage.absoluteString,
      "livenessFiles": livenessImages.map { $0.absoluteString },
    ]

    if let response = apiResponse,
       let responseData = try? JSONEncoder().encode(response),
       let responseDict = try? JSONSerialization.jsonObject(with: responseData) {
      result["apiResponse"] = responseDict
    }

    guard let jsonData = try? JSONSerialization.data(withJSONObject: result),
          let jsonString = String(data: jsonData, encoding: .utf8) else {
      onError("Error converting result to JSON" as NSString)
      return
    }

    onSuccess(jsonString as NSString)
  }

  func didError(error: Error) {
    print("📸 [SmileID View] ❌ Error: \(error.localizedDescription)")
    onError(error.localizedDescription as NSString)
  }
}