import { NativeModules, Platform, NativeEventEmitter } from 'react-native';

const KEYBOARD_STATUS_EVENT = 'keyboardStatus';

const LINKING_ERROR =
  `The package 'react-native-is-keyboard-connected' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error — global proxy flag is untyped
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const IsKeyboardConnectedModule = isTurboModuleEnabled
  ? require('../nativeSpecs/NativeIsKeyboardConnected').default
  : NativeModules.IsKeyboardConnected;

const IsKeyboardConnected = IsKeyboardConnectedModule
  ? IsKeyboardConnectedModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/** Status-change callback payload. */
export type StatusCallback = (e: { status: boolean }) => void;

/** Resolves whether a physical keyboard is currently connected. */
export const isKeyboardConnected = (): Promise<boolean> =>
  IsKeyboardConnected.isKeyboardConnected();

/**
 * Subscribes to physical-keyboard connect/disconnect events.
 * Returns a function that removes the listener.
 */
export const keyboardStatusListener = (callback: StatusCallback) => {
  const eventEmitter = new NativeEventEmitter(IsKeyboardConnected);
  const subscription = eventEmitter.addListener(
    KEYBOARD_STATUS_EVENT,
    callback
  );
  return () => subscription.remove();
};
