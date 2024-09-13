import React, {useState} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  PermissionsAndroid,
  Platform,
} from 'react-native';
import Geolocation from 'react-native-geolocation-service';
import {check, PERMISSIONS, request, RESULTS} from 'react-native-permissions';
import {sendLocationToBackend} from '../api/api';

const CurrentLocation = ({accessToken}) => {
  const [location, setLocation] = useState(null);
  const [backendResponse, setBackendResponse] = useState(null);

  const requestLocationPermission = async () => {
    try {
      if (Platform.OS === 'ios') {
        let status = await check(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
        console.log('iOS Current permission status:', status);

        if (status === RESULTS.DENIED || status === RESULTS.BLOCKED) {
          status = await request(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
          console.log('iOS Request result:', status);
        }

        if (status === RESULTS.GRANTED) {
          getCurrentLocation();
        } else {
          Alert.alert('위치 권한 필요', '위치 권한을 허용해주세요.');
        }
      } else {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        );
        console.log('Android Location permission:', granted);

        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
          getCurrentLocation();
        } else {
          Alert.alert('위치 권한 필요', '위치 권한을 허용해주세요.');
        }
      }
    } catch (error) {
      console.error('Error requesting location permission:', error);
    }
  };

  const getCurrentLocation = () => {
    Geolocation.getCurrentPosition(
      position => {
        const {longitude, latitude} = position.coords;

        console.log('현재 위치:', {latitude, longitude});
        setLocation({latitude, longitude});

        sendLocationToBackend(longitude, latitude, accessToken)
          .then(response => {
            console.log('Backend response:', response);
            setBackendResponse(response.result);
          })
          .catch(error => {
            console.error('Error sending location data:', error);
          });
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

  return (
    <View style={styles.container}>
      {backendResponse ? (
        <View>
          <Text style={styles.location}>{backendResponse.province}</Text>
          <Text style={styles.location}>{backendResponse.street}</Text>
        </View>
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
    flexDirection: 'column',
  },
  location: {
    fontSize: 18,
    color: '#fff',
    marginBottom: 7,
    textAlign: 'left',
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
