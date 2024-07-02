#import "IsKeyboardConnected.h"

#import "GameController/GameController.h"
#import "GameController/GCKeyboard.h"

@implementation IsKeyboardConnected{
    bool hasListeners;
}

NSString * const KEYBOARD_STATUS_EVENT = @"keyboardStatus";
NSString * const EVENT_PROP = @"status";

-(void)startObserving {
    hasListeners = YES;
}

-(void)stopObserving {
    hasListeners = NO;
}

- (instancetype)init
{
    if(self = [super init]) {
        if (@available(iOS 14.0, *)) {
            Class GCKeyboardClass = NSClassFromString(@"GCKeyboard");
            if(GCKeyboardClass != nil) {
                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasConnected:) name: @"GCKeyboardDidConnectNotification" object:nil];
                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasDisconnected:) name: @"GCKeyboardDidDisconnectNotification" object:nil];
            }
        }
    }
    
    return self;
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}


- (NSArray<NSString *> *)supportedEvents
{
    return @[KEYBOARD_STATUS_EVENT];
}

-(void) keyboardWasConnected: (NSNotification *) notification {
    if (hasListeners) {
        [self sendEventWithName: KEYBOARD_STATUS_EVENT body:@{EVENT_PROP: @(YES)}];
    }
}

-(void) keyboardWasDisconnected: (NSNotification *) notification {
    if (hasListeners) {
        [self sendEventWithName: KEYBOARD_STATUS_EVENT body:@{EVENT_PROP: @(NO)}];
    }
}


RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(isKeyboardConnected:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if (@available(iOS 14.0, *)) {
        Class GCKeyboardClass = NSClassFromString(@"GCKeyboard");
        if (GCKeyboardClass != nil) {
            bool isConnected = [GCKeyboardClass coalescedKeyboard] != nil;
            resolve(isConnected ? @(YES) : @(NO));
        } else {
            reject(@"GC_FRAMEWORK_LINKING_ERROR", @"The GameController framework is not linked. You can read more about this in the react-native-is-keyboard-connected Readme.md", nil);
        }
    } else {
        reject(@"IOS_VERSION_IS_NOT_SUPPORTED", @"iOS version less than 14.0", nil);
    }
}


// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
(const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeIsKeyboardConnectedSpecJSI>(params);
}
#endif

@end
