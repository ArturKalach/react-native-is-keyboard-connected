import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  isKeyboardConnected: () => Promise<boolean>;

  // RCTEventEmitter
  addListener: (eventName: string) => void;
  removeListeners: (count: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('IsKeyboardConnected');
