# react-native-is-keyboard-connected

React Native library for checking whether a keyboard is connected
The `new` and `old` architectures are supported!

# Installation

1. Download package with npm or yarn

```
npm i react-native-is-keyboard-connected
```

```
yarn add react-native-is-keyboard-connected
```

2. iOS only

Install pods

```
cd ios && pod install
```

Link keyboard(Game) binary with libraries

- Open xcode
- Select folder in the project bar
- Select target project
- Select `Build Phases`
- Expand `Link Binary With Libraries`
- Press plus icon
- You can search for `Game`
- Select `GameController.framework`,

See screenshot below:
<img src="/.github/images/link-binary-example.png" height="500" />

## Usage

```js
import {
  isKeyboardConnected,
  keyboardStatusListener,
  useIsKeyboardConnected,
} from 'react-native-is-keyboard-connected';

// ...

const isKeyboardConnected = useIsKeyboardConnected();

// Or you can handle it by your own

const removeListenerFn = keyboardStatusListener((e) => setResult(e.status));
isKeyboardConnected().then((isConnected) => setResult(isConnected));
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
