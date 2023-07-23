#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNIsKeyboardConnectedSpec.h"

@interface IsKeyboardConnected : RCTEventEmitter <NativeIsKeyboardConnectedSpec>
#else
#import <React/RCTBridgeModule.h>

@interface IsKeyboardConnected : RCTEventEmitter <RCTBridgeModule>
#endif

@end
