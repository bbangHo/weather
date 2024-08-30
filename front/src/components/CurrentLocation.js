import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {fetchWeatherData} from '../api/api';

const CurrentLocation = ({accessToken}) => {
  const [location, setLocation] = useState('');
  const memberId = 1;

  useEffect(() => {
    const fetchLocation = async () => {
      try {
        const weatherData = await fetchWeatherData(memberId, accessToken);
        if (weatherData.isSuccess) {
          setLocation(weatherData.result.location);
        } else {
          console.error('Failed to fetch location data:', weatherData.message);
        }
      } catch (error) {
        console.error('Error fetching location data:', error.message);
      }
    };

    fetchLocation();
  }, [accessToken]);

  return (
    <View style={styles.container}>
      {location ? (
        <Text style={styles.location}>{location}</Text>
      ) : (
        <Text style={styles.loading}>Loading...</Text>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 10,
    marginTop: 15,
    flexDirection: 'row',
  },
  location: {
    fontSize: 22,
    color: '#fff',
    marginTop: 10,
    marginBottom: 7,
  },
  loading: {
    fontSize: 18,
    color: '#999',
  },
});

export default CurrentLocation;
