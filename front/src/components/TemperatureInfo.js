import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Dimensions} from 'react-native';
import {fetchWeatherData} from '../api/api';

const TemperatureInfo = ({accessToken}) => {
  const [currentTmp, setCurrentTmp] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const weatherData = await fetchWeatherData(accessToken);
        if (weatherData.isSuccess) {
          setCurrentTmp(weatherData.result.currentTmp);

          if (
            weatherData.result.weatherPerHourList &&
            weatherData.result.weatherPerHourList.length > 0
          ) {
            console.log(
              'First item in weatherPerHourList:',
              weatherData.result.weatherPerHourList[0],
            );
          } else {
            console.log('weatherPerHourList is empty or undefined');
          }
        } else {
          console.error('Failed to fetch weather data:', weatherData.message);
        }
      } catch (error) {
        console.error('Error fetching weather data:', error.message);
      }
    };

    if (accessToken) {
      fetchData();
    }
  }, [accessToken]);

  const windowWidth = Dimensions.get('window').width;
  const marginLeftValue = windowWidth * 0.07;
  const marginBottomValue = windowWidth * 0.03;

  return (
    <View style={styles.container}>
      {currentTmp !== null ? (
        <Text
          style={[
            styles.temperature,
            {marginLeft: marginLeftValue, marginBottom: marginBottomValue},
          ]}>
          {currentTmp}°C
        </Text>
      ) : (
        <Text
          style={[
            styles.loadingOrErrorText,
            {marginLeft: marginLeftValue, marginBottom: marginBottomValue},
          ]}>
          Loading ...
        </Text>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'flex-start',
    borderRadius: 10,
  },
  temperature: {
    fontSize: 22,
    color: '#fff',
    marginTop: 15,
  },
  loadingOrErrorText: {
    fontSize: 16,
    color: '#fff',
    marginTop: 15,
  },
});

export default TemperatureInfo;
