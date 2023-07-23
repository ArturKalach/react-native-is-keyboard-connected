package com.iskeyboardconnected;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;

abstract class IsKeyboardConnectedSpec extends ReactContextBaseJavaModule {
  IsKeyboardConnectedSpec(ReactApplicationContext context) {
    super(context);
  }

  public abstract void isKeyboardConnected(Promise promise);
}
