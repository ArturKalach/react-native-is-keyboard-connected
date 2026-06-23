![react-native-is-keyboard-connected](/.github/images/react-native-is-keyboard-connected.png)

# React Native Is Keyboard Connected

<div>
  <img align="right" width="35%" src="/.github/images/react-native-is-keyboard-connected-example.gif" alt="Demo of a React Native app reacting to a physical keyboard connecting and disconnecting">
</div>

Native-first React Native module that reports whether a **physical (hardware) keyboard**
is connected, and emits an event whenever that connection state changes — on iOS and
Android.

- 🔌 **Connection status** — one-shot `isKeyboardConnected()` query
- 📡 **Live updates** — subscribe to connect / disconnect events
- ⚛️ **Hook** — `useIsKeyboardConnected()` for drop-in React state
- ⚡ New Architecture · Old Architecture · Bridgeless

> [!TIP]
> Need more than connection status? This module is also bundled — alongside screen-reader
> focus order, physical-keyboard support, and iOS accessibility containers — into the
> all-in-one [`react-native-a11y`](https://www.npmjs.com/package/react-native-a11y)
> toolkit. Install the focused package for just this capability, or `react-native-a11y`
> for the complete set.

## Installation

```sh
yarn add react-native-is-keyboard-connected
cd ios && pod install
```

That's it — on iOS the **GameController** framework is linked automatically via the
podspec. No manual "Link Binary With Libraries" step is required.

<details>
  <summary>Why GameController?</summary>

GameController is the only App Store–safe way to obtain hardware keyboard connection
status (`GCKeyboard`, iOS 14+). Other approaches are workarounds that risk rejection
during App Store review.

</details>

## Usage

### Hardware keyboard

Backed by the native module (GameController on iOS, `Configuration` on Android).

```tsx
import {
  isKeyboardConnected,
  keyboardStatusListener,
  useIsKeyboardConnected,
  useIsKeyboardConnectedRef,
} from 'react-native-is-keyboard-connected';

// Hook — re-renders when a keyboard connects or disconnects
const connected = useIsKeyboardConnected();

// Ref hook — same live value without re-rendering (read `ref.current` in callbacks)
const connectedRef = useIsKeyboardConnectedRef();

// Or drive it yourself:
// one-shot query
isKeyboardConnected().then((isConnected) => setResult(isConnected));

// subscribe to changes — returns an unsubscribe function
const removeListener = keyboardStatusListener((e) => setResult(e.status));
```

### Screen reader

> **Note:** These helpers only wrap React Native's default
> [`AccessibilityInfo`](https://reactnative.dev/docs/accessibilityinfo) API — no
> native module and no extra linking. They're syntax sugar that mirrors the
> keyboard API (same `{ status }` listener payload) so both can be used the same way.

```tsx
import {
  isScreenReaderEnabled,
  screenReaderStatusListener,
  useIsScreenReaderEnabled,
  useIsScreenReaderEnabledRef,
} from 'react-native-is-keyboard-connected';

// Hook — re-renders when VoiceOver / TalkBack is toggled
const enabled = useIsScreenReaderEnabled();

// Ref hook — same live value without re-rendering
const enabledRef = useIsScreenReaderEnabledRef();

// Or drive it yourself:
isScreenReaderEnabled().then((isEnabled) => setResult(isEnabled));

const removeListener = screenReaderStatusListener((e) => setResult(e.status));
```

## API

### Hardware keyboard (native module)

| Export | Purpose |
| :-- | :-- |
| `useIsKeyboardConnected()` | Hook returning the current connection state, updated on change. |
| `useIsKeyboardConnectedRef()` | Ref variant — `.current` holds the latest state without re-rendering. |
| `isKeyboardConnected()` | `Promise<boolean>` — one-shot query of the current state. |
| `keyboardStatusListener(cb)` | Subscribe to `{ status: boolean }` change events; returns an unsubscribe function. |

### Screen reader (wraps RN `AccessibilityInfo`)

| Export | Purpose |
| :-- | :-- |
| `useIsScreenReaderEnabled()` | Hook returning whether a screen reader is enabled, updated on change. |
| `useIsScreenReaderEnabledRef()` | Ref variant — `.current` holds the latest state without re-rendering. |
| `isScreenReaderEnabled()` | `Promise<boolean>` — one-shot query of the current state. |
| `screenReaderStatusListener(cb)` | Subscribe to `{ status: boolean }` change events; returns an unsubscribe function. |

## Architecture support

| Capability | Supported |
| :-- | :-- |
| New Architecture (Fabric / Turbo Modules) | ✅ |
| Old Architecture (Bridge) | ✅ |
| Bridgeless mode | ✅ |

## Migrating from 1.0.0

`1.1.0` is **backward compatible** — no breaking changes. `isKeyboardConnected`,
`keyboardStatusListener`, and `useIsKeyboardConnected` keep the same signatures,
so existing code needs no changes.

New in `1.1.0` (all additive):

- `useIsKeyboardConnectedRef` — ref variant of `useIsKeyboardConnected`.
- `isScreenReaderEnabled` / `screenReaderStatusListener` /
  `useIsScreenReaderEnabled` / `useIsScreenReaderEnabledRef` — screen-reader
  helpers over RN's `AccessibilityInfo` (see the [note](#screen-reader) above).

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
