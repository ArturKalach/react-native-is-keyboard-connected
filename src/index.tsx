import { useEffect, useState } from 'react';
import { NativeModules, Platform, NativeEventEmitter } from 'react-native';

const KEYBOARD_STATUS_EVENT = 'keyboardStatus';

const LINKING_ERROR =
  `The package 'react-native-is-keyboard-connected' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const IsKeyboardConnectedModule = isTurboModuleEnabled
  ? require('./nativeSpecs/NativeIsKeyboardConnected').default
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

export const isKeyboardConnected = (): Promise<boolean> => {
  return IsKeyboardConnected.isKeyboardConnected();
};

export type StatusCallback = (e: { status: boolean }) => void;

export const keyboardStatusListener = (callback: StatusCallback) => {
  const eventEmitter = new NativeEventEmitter(IsKeyboardConnected);
  const eventListener = eventEmitter.addListener(
    KEYBOARD_STATUS_EVENT,
    callback
  );
  return () => eventListener.remove();
};

export const useIsKeyboardConnected = () => {
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    const removeListener = keyboardStatusListener((e) =>
      setIsConnected(e.status)
    );
    isKeyboardConnected().then((status: boolean) => setIsConnected(status));

    return removeListener;
  }, []);

  return isConnected;
};
