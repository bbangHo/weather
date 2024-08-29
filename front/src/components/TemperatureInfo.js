import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {fetchWeatherData} from '../api/api';

const TemperatureInfo = ({accessToken}) => {
  const [currentTmp, setCurrentTmp] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const weatherData = await fetchWeatherData(1, accessToken);
        if (weatherData.isSuccess) {
          setCurrentTmp(weatherData.result.currentTmp);
        } else {
          console.error('Failed to fetch weather data:', weatherData.message);
        }
      } catch (error) {
        console.error('Error fetching weather data:', error.message);
      }
    };

    fetchData();
  }, [accessToken]);

  return (
    <View style={styles.container}>
      {currentTmp !== null ? (
        <Text style={styles.temperature}>{currentTmp}°C</Text>
      ) : (
        <Text style={styles.temperature}>Loading...</Text>
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
    marginLeft: 27,
  },
});

export default TemperatureInfo;
