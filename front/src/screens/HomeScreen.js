import React from 'react';
import {ScrollView, View, Text, StyleSheet} from 'react-native';
import {PanGestureHandler, State} from 'react-native-gesture-handler';
import {useNavigation} from '@react-navigation/native';

const HomeScreen = () => {
  const navigation = useNavigation();

  const handleGesture = event => {
    if (
      event.nativeEvent.translationX > 200 &&
      event.nativeEvent.state === State.END
    ) {
      navigation.navigate('PostCreationScreen');
    }
  };

  return (
    <PanGestureHandler
      onGestureEvent={handleGesture}
      onHandlerStateChange={handleGesture}>
      <ScrollView contentContainerStyle={styles.container}>
        <View style={styles.container}>
          <Text>My</Text>
        </View>
      </ScrollView>
    </PanGestureHandler>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default HomeScreen;
