
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNIsKeyboardConnectedSpec.h"

@interface IsKeyboardConnected : NSObject <NativeIsKeyboardConnectedSpec>
#else
#import <React/RCTBridgeModule.h>

@interface IsKeyboardConnected : NSObject <RCTBridgeModule>
#endif

@end
