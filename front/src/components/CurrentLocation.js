import React, {useEffect, useState} from 'react';
import {View, Text, TouchableOpacity, StyleSheet, Alert} from 'react-native';
import Geolocation from 'react-native-geolocation-service';
import {check, PERMISSIONS, request, RESULTS} from 'react-native-permissions';
import {fetchWeatherData, sendLocationToBackend} from '../api/api';

const CurrentLocation = ({accessToken}) => {
  const [location, setLocation] = useState('');
  const memberId = 1;

  const requestLocationPermission = async () => {
    try {
      let status = await check(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);

      console.log('Current permission status:', status);

      if (status === RESULTS.DENIED || status === RESULTS.BLOCKED) {
        status = await request(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
        console.log('Request result:', status);
      }

      if (status === RESULTS.GRANTED) {
        getCurrentLocation();
      } else {
        Alert.alert('위치 권한 필요', '위치 권한을 허용해주세요.');
      }
    } catch (error) {
      console.error('Error requesting location permission:', error);
    }
  };

  const getCurrentLocation = () => {
    Geolocation.getCurrentPosition(
      position => {
        const {latitude, longitude} = position.coords;
        console.log('현재 위치:', latitude, longitude);

        sendLocationToBackend(latitude, longitude, accessToken);
      },
      error => {
        console.error('Error getting current position:', error);
        Alert.alert(
          '위치 정보를 가져올 수 없습니다.',
          '위치 권한을 확인해주세요.',
        );
      },
      {enableHighAccuracy: true, timeout: 15000, maximumAge: 10000},
    );
  };

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
        <TouchableOpacity
          style={styles.button}
          onPress={requestLocationPermission}>
          <Text style={styles.buttonText}>위치 정보를 등록해주세요</Text>
        </TouchableOpacity>
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
  button: {
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    paddingVertical: 10,
    paddingHorizontal: 20,
    marginHorizontal: 10,
    borderRadius: 5,
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    textAlign: 'center',
  },
});

export default CurrentLocation;
