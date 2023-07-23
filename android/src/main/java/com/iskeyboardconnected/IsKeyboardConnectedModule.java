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
  private final String ON_CONFIGURATION_CHANGED = "onConfigurationChanged";
  private final String NEW_CONFIG = "newConfig";
  private final ReactApplicationContext reactContext;
  private final BroadcastReceiver receiver;

  IsKeyboardConnectedModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;

    context.addLifecycleEventListener(this);
    receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        final Configuration newConfig = intent.getParcelableExtra(NEW_CONFIG);
        keyboardChanged(newConfig.hardKeyboardHidden == HARDKEYBOARDHIDDEN_NO);
      }
    };
    keyboardChanged(isConnected());
  }

  private boolean isConnected() {
    final int keyboard = reactContext.getResources().getConfiguration().keyboard;
    boolean isConnected = keyboard != KEYBOARD_UNDEFINED && keyboard != KEYBOARD_NOKEYS;
    return isConnected;
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

  @ReactMethod
  public void addListener(String eventName) {

  }

  @Override
  @ReactMethod
  public void removeListeners(double count) {

  }


  @Override
  public void onHostResume() {
    final Activity activity = reactContext.getCurrentActivity();

    if (activity == null) {
      return;
    }
    activity.registerReceiver(receiver, new IntentFilter(ON_CONFIGURATION_CHANGED));
  }

  @Override
  public void onHostPause() {
    final Activity activity = reactContext.getCurrentActivity();
    if (activity == null) return;
    try {
      activity.unregisterReceiver(receiver);
    } catch (java.lang.IllegalArgumentException e) {
    }
  }

  @Override
  public void onHostDestroy() {

  }
}
