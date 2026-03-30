#if RCT_NEW_ARCH_ENABLED
#import "SmartSelfieEnrollmentView.h"

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

@interface SmartSelfieEnrollmentView () <RCTSmartSelfieEnrollmentViewViewProtocol>
@end

@implementation SmartSelfieEnrollmentView {
  SmartSelfieEnrollmentViewProvider *_provider;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<SmartSelfieEnrollmentViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const SmartSelfieEnrollmentViewProps>();
    _props = defaultProps;

    _provider = [[SmartSelfieEnrollmentViewProvider alloc] init];
    self.contentView = _provider;

    __weak SmartSelfieEnrollmentView *weakSelf = self;
    _provider.onSuccess = ^(NSString *json) {
      SmartSelfieEnrollmentView *strongSelf = weakSelf; if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const SmartSelfieEnrollmentViewEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      SmartSelfieEnrollmentViewEventEmitter::OnSuccess ev{};
      ev.result = std::string([json UTF8String]);
      emitter->onSuccess(std::move(ev));
    };

    _provider.onError = ^(NSString *errorString) {
      SmartSelfieEnrollmentView *strongSelf = weakSelf; if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const SmartSelfieEnrollmentViewEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      SmartSelfieEnrollmentViewEventEmitter::OnError ev{};
      ev.error = std::string([errorString UTF8String]);
      emitter->onError(std::move(ev));
    };
  }
  return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &newViewProps = *std::static_pointer_cast<SmartSelfieEnrollmentViewProps const>(props);

  NSLog(@"📸 [SmileID ObjC] updateProps - userId: %s, jobId: %s",
    newViewProps.userId.c_str(), newViewProps.jobId.c_str());

  [super updateProps:props oldProps:oldProps];

  _provider.userId = [NSString stringWithUTF8String:newViewProps.userId.c_str()];
  _provider.jobId = [NSString stringWithUTF8String:newViewProps.jobId.c_str()];
  _provider.allowAgentMode = newViewProps.allowAgentMode;
  _provider.showAttribution = newViewProps.showAttribution;
  _provider.showInstructions = newViewProps.showInstructions;
  _provider.skipApiSubmission = newViewProps.skipApiSubmission;
  _provider.extraPartnerParams = [NSString stringWithUTF8String:newViewProps.extraPartnerParams.c_str()];

  NSLog(@"📸 [SmileID ObjC] Set userId: %@, jobId: %@, extraPartnerParams: %@",
    _provider.userId, _provider.jobId, _provider.extraPartnerParams);

  [_provider buildView];
}

@end

Class<RCTComponentViewProtocol> SmartSelfieEnrollmentViewCls(void)
{
  return SmartSelfieEnrollmentView.class;
}

#endif