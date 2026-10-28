package com.iskeyboardconnected;

import static android.content.res.Configuration.KEYBOARD_NOKEYS;
import static android.content.res.Configuration.KEYBOARD_UNDEFINED;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.InputDevice;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class IsKeyboardConnectedModule extends com.iskeyboardconnected.IsKeyboardConnectedSpec implements LifecycleEventListener {
  public static final String KEYBOARD_STATUS_EVENT = "keyboardStatus";
  public static final String EVENT_PROP = "status";
  public static final String NAME = "IsKeyboardConnected";
  // Letter keys. EventHub sets ALPHAKEY from this map. Pixel 17 still reports
  // getKeyboardType() as NON_ALPHABETIC when the keyboard is merged with a mouse.
  private static final int[] ALPHABETIC_PROBE = new int[] {
    KeyEvent.KEYCODE_Q,
    KeyEvent.KEYCODE_A,
  };
  private final ReactApplicationContext reactContext;
  private final BroadcastReceiver receiver;
  private final InputManager.InputDeviceListener inputDeviceListener;
  private boolean isBroadcastRegistered = false;
  private boolean isInputListenerRegistered = false;
  private int listenerCount = 0;

  IsKeyboardConnectedModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;

    context.addLifecycleEventListener(this);
    receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())) {
          // Same predicate as the query. hardKeyboardHidden stays YES on Pixel 17
          // for the whole time an external keyboard is attached.
          keyboardChanged(isConnected());
        }
      }
    };
    inputDeviceListener = new InputManager.InputDeviceListener() {
      @Override
      public void onInputDeviceAdded(int deviceId) {
        keyboardChanged(isConnected());
      }

      @Override
      public void onInputDeviceRemoved(int deviceId) {
        keyboardChanged(isConnected());
      }

      @Override
      public void onInputDeviceChanged(int deviceId) {
        keyboardChanged(isConnected());
      }
    };
    keyboardChanged(isConnected());
  }

  private boolean isConnected() {
    final int keyboard = reactContext.getResources().getConfiguration().keyboard;
    final boolean configurationKeyboard =
      keyboard != KEYBOARD_UNDEFINED && keyboard != KEYBOARD_NOKEYS;
    final boolean externalAlphabetic = hasExternalAlphabeticKeyboard();
    return configurationKeyboard || externalAlphabetic;
  }

  /**
   * Pixel Android 17 keeps Configuration.keyboard at KEYBOARD_NOKEYS for MX Keys.
   * The keyboard and its mouse interface share one InputDevice whose
   * getKeyboardType() is NON_ALPHABETIC, while EventHub still flags ALPHAKEY.
   */
  private boolean hasExternalAlphabeticKeyboard() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      return false;
    }
    final int[] ids = InputDevice.getDeviceIds();
    for (int id : ids) {
      final InputDevice device = InputDevice.getDevice(id);
      if (device == null || device.isVirtual() || !device.isExternal()) {
        continue;
      }
      final int sources = device.getSources();
      if ((sources & InputDevice.SOURCE_KEYBOARD) != InputDevice.SOURCE_KEYBOARD) {
        continue;
      }
      if (device.getKeyboardType() == InputDevice.KEYBOARD_TYPE_ALPHABETIC) {
        return true;
      }
      final boolean[] present = device.hasKeys(ALPHABETIC_PROBE);
      final boolean hasLetters =
        present.length == ALPHABETIC_PROBE.length && present[0] && present[1];
      if (hasLetters) {
        return true;
      }
    }
    return false;
  }

  public void keyboardChanged(Boolean info) {
    final WritableMap params = Arguments.createMap();
    params.putBoolean(EVENT_PROP, info);
    sendEvent(reactContext, KEYBOARD_STATUS_EVENT, params);
  }

  private void sendEvent(ReactApplicationContext reactContext,
                         String eventName,
                         @NonNull WritableMap params) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void isKeyboardConnected(Promise promise) {
    boolean isConnected = this.isConnected();
    promise.resolve(isConnected);
  }

  private void registerBroadcast() {
    if (isBroadcastRegistered) return;
    isBroadcastRegistered = true;
    final Activity activity = reactContext.getCurrentActivity();

    if (activity != null) {
      activity.registerReceiver(receiver, new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
    }
  }

  private void unregisterBroadcast() {
    if (!isBroadcastRegistered) return;
    isBroadcastRegistered = false;

    final Activity activity = reactContext.getCurrentActivity();
    if (activity == null) return;
    try {
      activity.unregisterReceiver(receiver);
    } catch (java.lang.IllegalArgumentException e) {
    }
  }

  private void registerInputListener() {
    if (isInputListenerRegistered) return;
    final InputManager inputManager =
      (InputManager) reactContext.getSystemService(Context.INPUT_SERVICE);
    if (inputManager == null) return;
    inputManager.registerInputDeviceListener(
      inputDeviceListener,
      new Handler(Looper.getMainLooper())
    );
    isInputListenerRegistered = true;
  }

  private void unregisterInputListener() {
    if (!isInputListenerRegistered) return;
    isInputListenerRegistered = false;
    final InputManager inputManager =
      (InputManager) reactContext.getSystemService(Context.INPUT_SERVICE);
    if (inputManager == null) return;
    inputManager.unregisterInputDeviceListener(inputDeviceListener);
  }

  @Override
  @ReactMethod
  public void addListener(String eventName) {
    if (listenerCount == 0) {
      registerBroadcast();
      registerInputListener();
    }

    listenerCount += 1;
  }

  @Override
  @ReactMethod
  public void removeListeners(double count) {
    listenerCount -= count;
    if (listenerCount == 0) {
      unregisterBroadcast();
      unregisterInputListener();
    }
  }

  @Override
  public void onHostResume() {
    if (listenerCount != 0) {
      registerBroadcast();
      registerInputListener();
      keyboardChanged(isConnected());
    }
  }

  @Override
  public void onHostPause() {
    unregisterBroadcast();
    unregisterInputListener();
  }

  @Override
  public void onHostDestroy() {

  }
}
