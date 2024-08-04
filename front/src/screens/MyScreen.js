import React from 'react';
import {View, Text, StyleSheet} from 'react-native';

const MyScreen = () => {
  return (
    <View style={styles.container}>
      <Text>My</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default MyScreen;
