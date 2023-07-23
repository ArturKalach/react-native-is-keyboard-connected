import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { useIsKeyboardConnected } from 'react-native-is-keyboard-connected';

export default function App() {
  const isConnected = useIsKeyboardConnected();

  return (
    <View style={styles.container}>
      <Text>Result: {isConnected ? 'connected' : 'is not connected'}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
