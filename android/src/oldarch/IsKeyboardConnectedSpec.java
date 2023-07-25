package com.iskeyboardconnected;

import com.facebook.common.internal.DoNotStrip;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;

abstract class IsKeyboardConnectedSpec extends ReactContextBaseJavaModule {
  IsKeyboardConnectedSpec(ReactApplicationContext context) {
    super(context);
  }

  public abstract void isKeyboardConnected(Promise promise);



  public abstract void addListener(String eventName);
  public abstract void removeListeners(double count);
}
