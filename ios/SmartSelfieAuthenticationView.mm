#if RCT_NEW_ARCH_ENABLED
#import "SmartSelfieAuthenticationView.h"

#if __has_include("RnWrap/RnWrap-Swift.h")
#import "RnWrap/RnWrap-Swift.h"
#else
#import "RnWrap-Swift.h"
#endif

#import <react/renderer/components/RnWrapViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/RnWrapViewSpec/EventEmitters.h>
#import <react/renderer/components/RnWrapViewSpec/Props.h>
#import <react/renderer/components/RnWrapViewSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

#include <optional>

using namespace facebook::react;

@interface SmartSelfieAuthenticationView () <RCTSmartSelfieAuthenticationViewViewProtocol>
@end

@implementation SmartSelfieAuthenticationView {
  SmartSelfieAuthenticationViewProvider *_provider;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<SmartSelfieAuthenticationViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const SmartSelfieAuthenticationViewProps>();
    _props = defaultProps;

    _provider = [[SmartSelfieAuthenticationViewProvider alloc] init];

    if (!_provider) {
      NSLog(@"📸 [SmileID Auth ObjC] ⚠️ Failed to create provider");
      return self;
    }

    self.contentView = _provider;

    __weak SmartSelfieAuthenticationView *weakSelf = self;
    _provider.onSuccess = ^(NSString *json) {
      SmartSelfieAuthenticationView *strongSelf = weakSelf; if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const SmartSelfieAuthenticationViewEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      SmartSelfieAuthenticationViewEventEmitter::OnSuccess ev{};
      ev.result = std::string([json UTF8String]);
      emitter->onSuccess(std::move(ev));
    };

    _provider.onError = ^(NSString *errorString) {
      SmartSelfieAuthenticationView *strongSelf = weakSelf; if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const SmartSelfieAuthenticationViewEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      SmartSelfieAuthenticationViewEventEmitter::OnError ev{};
      ev.error = std::string([errorString UTF8String]);
      emitter->onError(std::move(ev));
    };
  }
  return self;
}

- (void)updateProps:(const Props::Shared &)props oldProps:(const Props::Shared &)oldProps
{
  const auto &newViewProps = *std::static_pointer_cast<const SmartSelfieAuthenticationViewProps>(props);

  NSLog(@"📸 [SmileID Auth ObjC] updateProps called - userId: %s, jobId: %s",
    newViewProps.userId.c_str(), newViewProps.jobId.c_str());

  // Call super FIRST
  [super updateProps:props oldProps:oldProps];

  // Set all props on the provider
  _provider.userId = [NSString stringWithUTF8String:newViewProps.userId.c_str()];
  _provider.jobId = [NSString stringWithUTF8String:newViewProps.jobId.c_str()];
  _provider.allowAgentMode = newViewProps.allowAgentMode;
  _provider.showAttribution = newViewProps.showAttribution;
  _provider.showInstructions = newViewProps.showInstructions;
  _provider.skipApiSubmission = newViewProps.skipApiSubmission;

  NSLog(@"📸 [SmileID Auth ObjC] Set userId: %@, jobId: %@", _provider.userId, _provider.jobId);

  // Build view AFTER all props are set
  [_provider buildView];
}

@end
#endif
