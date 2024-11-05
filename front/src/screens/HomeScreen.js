import React, {useState, useEffect} from 'react';
import {
  ScrollView,
  View,
  StyleSheet,
  StatusBar,
  Dimensions,
  Platform,
} from 'react-native';
import ToggleViewButton from '../components/ToggleViewButton';
import WeatherInfoSlider from '../components/WeatherInfoSlider';
import CurrentLocation from '../components/CurrentLocation';
import TemperatureInfo from '../components/TemperatureInfo';
import Posts from '../components/Posts';
import HourlyForecast from '../components/HourlyForecast';
import AirQuality from '../components/AirQuality';
import WeatherGraph from '../components/WeatherGraph';
import KakaoShareButton from '../components/KakaoShareButton';
import {useNavigation} from '@react-navigation/native';

const {height} = Dimensions.get('window');

const HomeScreen = ({accessToken}) => {
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

  return (
    <ScrollView
      style={[styles.container, {backgroundColor: backgroundColor}]}
      contentContainerStyle={styles.contentContainer}
      scrollEnabled={true}
      nestedScrollEnabled={true}>
      <StatusBar hidden={true} />
      <View style={styles.topSpacer} />
      <View style={styles.topContainer}>
        <WeatherInfoSlider accessToken={accessToken} />
        <View style={styles.rightContainer}>
          <CurrentLocation accessToken={accessToken} />
          <TemperatureInfo accessToken={accessToken} />
          <ToggleViewButton showText={showText} setShowText={setShowText} />
        </View>
      </View>

      <HourlyForecast accessToken={accessToken} showText={showText} />
      <AirQuality accessToken={accessToken} />
      <Posts accessToken={accessToken} />

      <View style={styles.shareButtonContainer}>
        <KakaoShareButton accessToken={accessToken} />
      </View>

      <WeatherGraph accessToken={accessToken} />
    </ScrollView>
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
    height: Platform.OS === 'ios' ? height * 0.06 : height * 0.03,
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
  shareButtonContainer: {
    marginTop: -7,
    marginVertical: 10,
    alignItems: 'center',
  },
});

export default HomeScreen;
