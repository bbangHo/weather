import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, StatusBar} from 'react-native';
import CurrentLocation from '../components/CurrentLocation';
import TemperatureInfo from '../components/TemperatureInfo';
import PostScroll from '../components/PostScroll';
import WeatherShareButton from '../components/WeatherShareButton';
import {fetchWeatherData} from '../api/api';

const CommunityScreen = ({accessToken, memberId}) => {
  const [weatherData, setWeatherData] = useState(null);
  const [backgroundColor, setBackgroundColor] = useState('#2f5af4');

  useEffect(() => {
    const currentHour = new Date().getHours();
    if (currentHour >= 6 && currentHour < 18) {
      setBackgroundColor('#2f5af4');
    } else {
      setBackgroundColor('#1D2837');
    }
  }, []);

  useEffect(() => {
    const getWeatherData = async () => {
      try {
        const data = await fetchWeatherData(memberId, accessToken);
        if (data.isSuccess) {
          setWeatherData(data.result);
        } else {
          console.error('Failed to fetch weather data:', data.message);
        }
      } catch (error) {
        console.error('Error fetching weather data:', error.message);
      }
    };

    getWeatherData();
  }, [accessToken, memberId]);

  return (
    <View style={[styles.container, {backgroundColor: backgroundColor}]}>
      <StatusBar hidden={true} />
      <View style={styles.topSpacer} />
      <View style={styles.topContainer}>
        <WeatherShareButton />
        <View style={styles.rightContainer}>
          <CurrentLocation accessToken={accessToken} memberId={memberId} />
          <TemperatureInfo accessToken={accessToken} />
        </View>
      </View>
      <Text style={styles.text}>
        ‘추위를 많이 타는’ 유형이 가장 많이 공감했어요
      </Text>
      <PostScroll accessToken={accessToken} memberId={memberId} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  topSpacer: {
    height: 50,
  },
  topContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '95%',
    paddingBottom: 15,
  },
  rightContainer: {
    width: '50%',
    justifyContent: 'space-between',
    paddingBottom: 10,
  },
  text: {
    color: '#fff',
    textAlign: 'center',
    paddingBottom: 10,
  },
});

export default CommunityScreen;
