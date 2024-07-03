package com.iskeyboardconnected;

import static android.content.res.Configuration.HARDKEYBOARDHIDDEN_NO;
import static android.content.res.Configuration.KEYBOARD_NOKEYS;
import static android.content.res.Configuration.KEYBOARD_UNDEFINED;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

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
  private final ReactApplicationContext reactContext;
  private final BroadcastReceiver receiver;
  private boolean isBroadcastRegistered = false;
  private int listenerCount = 0;

  IsKeyboardConnectedModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;

    context.addLifecycleEventListener(this);
    receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())) {
          Configuration newConfig = context.getResources().getConfiguration();
          keyboardChanged(newConfig.hardKeyboardHidden == HARDKEYBOARDHIDDEN_NO);
        }
      }
    };
    keyboardChanged(isConnected());
  }

  private boolean isConnected() {
    final int keyboard = reactContext.getResources().getConfiguration().keyboard;
    return keyboard != KEYBOARD_UNDEFINED && keyboard != KEYBOARD_NOKEYS;
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

  @Override
  @ReactMethod
  public void addListener(String eventName) {
    if (listenerCount == 0) {
      registerBroadcast();
    }

    listenerCount += 1;
  }

  @Override
  @ReactMethod
  public void removeListeners(double count) {
    listenerCount -= count;
    if (listenerCount == 0) {
      unregisterBroadcast();
    }
  }

  @Override
  public void onHostResume() {
    if (listenerCount != 0) {
      registerBroadcast();
    }
  }

  @Override
  public void onHostPause() {
    unregisterBroadcast();
  }

  @Override
  public void onHostDestroy() {

  }
}
