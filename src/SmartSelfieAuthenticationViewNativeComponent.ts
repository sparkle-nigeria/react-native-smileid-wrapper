import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ViewProps } from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

export type SmartSelfieAuthSuccessEvent = Readonly<{ result: string }>;
export type SmartSelfieAuthErrorEvent = Readonly<{ error: string }>;

interface NativeProps extends ViewProps {
  userId?: string;
  jobId?: string;
  allowNewEnroll?: boolean;
  allowAgentMode?: boolean;
  showAttribution?: boolean;
  showInstructions?: boolean;
  skipApiSubmission?: boolean;
  extraPartnerParams?: string; // JSON string since codegen doesn't support maps
  smileSensitivity?: string; // 'auto' | 'manual' or null

  onSuccess?: DirectEventHandler<SmartSelfieAuthSuccessEvent>;
  onError?: DirectEventHandler<SmartSelfieAuthErrorEvent>;
}

export default codegenNativeComponent<NativeProps>('SmartSelfieAuthenticationView');
