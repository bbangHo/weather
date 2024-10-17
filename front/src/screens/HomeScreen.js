import React, {useState, useEffect} from 'react';
import {ScrollView, View, StyleSheet, StatusBar} from 'react-native';
import {PanGestureHandler, State} from 'react-native-gesture-handler';
import ToggleViewButton from '../components/ToggleViewButton';
import WeatherInfoSlider from '../components/WeatherInfoSlider';
import CurrentLocation from '../components/CurrentLocation';
import TemperatureInfo from '../components/TemperatureInfo';
import Posts from '../components/Posts';
import HourlyForecast from '../components/HourlyForecast';
import AirQuality from '../components/AirQuality';
import WeatherGraph from '../components/WeatherGraph';
import {useNavigation} from '@react-navigation/native';

const HomeScreen = ({accessToken, memberId}) => {
  const navigation = useNavigation();
  const [showText, setShowText] = useState(false);
  const [backgroundColor, setBackgroundColor] = useState('#2f5af4');

  useEffect(() => {
    const currentHour = new Date().getHours();
    if (currentHour >= 6 && currentHour < 18) {
      setBackgroundColor('#2f5af4');
    } else {
      setBackgroundColor('#1D2837');
    }
  }, []);

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
      <ScrollView
        style={[styles.container, {backgroundColor: backgroundColor}]}
        contentContainerStyle={styles.contentContainer}>
        <StatusBar hidden={true} />
        <View style={styles.topSpacer} />
        <View style={styles.topContainer}>
          <WeatherInfoSlider accessToken={accessToken} memberId={memberId} />
          <View style={styles.rightContainer}>
            <CurrentLocation accessToken={accessToken} memberId={memberId} />
            <TemperatureInfo accessToken={accessToken} memberId={memberId} />
            <ToggleViewButton showText={showText} setShowText={setShowText} />
          </View>
        </View>
        <HourlyForecast
          accessToken={accessToken}
          memberId={memberId}
          showText={showText}
        />
        <AirQuality accessToken={accessToken} memberId={memberId} />
        <Posts accessToken={accessToken} memberId={memberId} />
        <WeatherGraph accessToken={accessToken} memberId={memberId} />
      </ScrollView>
    </PanGestureHandler>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  contentContainer: {
    paddingBottom: 20,
    alignItems: 'center',
  },
  topSpacer: {
    height: 50,
  },
  topContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '95%',
  },
  rightContainer: {
    width: '50%',
    justifyContent: 'space-between',
  },
});

export default HomeScreen;
