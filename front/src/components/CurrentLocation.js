import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  Image,
} from 'react-native';
import {fetchUserLocation, fetchWeatherData} from '../api/api';

const CurrentLocation = ({accessToken}) => {
  const [userLocation, setUserLocation] = useState(null);
  const [weatherData, setWeatherData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadUserData = async () => {
      try {
        const location = await fetchUserLocation(accessToken);
        console.log('Fetched user location:', location);
        setUserLocation(location);

        const weather = await fetchWeatherData(accessToken);
        console.log('Fetched weather data:', weather);
        setWeatherData(weather.result);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching data:', error);
        Alert.alert('데이터를 불러오는 중 오류가 발생했습니다.', error.message);
        setLoading(false);
      }
    };

    loadUserData();
  }, [accessToken]);

  const getWeatherIcon = currentSkyType => {
    switch (currentSkyType) {
      case 'CLEAR':
        return require('../../assets/images/icon_clear.png');
      case 'PARTLYCLOUDY':
        return require('../../assets/images/icon_partlycloudy.png');
      case 'CLOUDY':
        return require('../../assets/images/icon_cloudy.png');
      default:
        return require('../../assets/images/icon_cloudy.png');
    }
  };

  if (loading) {
    return (
      <View style={styles.container}>
        <Text style={styles.loadingText}>데이터를 불러오는 중...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {userLocation && weatherData ? (
        <View>
          <View style={styles.locationContainer}>
            <Text style={styles.location}>{userLocation.city}</Text>
            <Image
              source={getWeatherIcon(weatherData.currentSkyType)}
              style={styles.weatherIcon}
            />
          </View>
          <Text style={styles.location}>{userLocation.street}</Text>
        </View>
      ) : (
        <Text style={styles.errorText}>데이터를 불러오지 못했습니다.</Text>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 10,
    marginTop: -15,
    marginLeft: 10,
    flexDirection: 'column',
  },
  locationContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: -30,
  },
  location: {
    fontSize: 20,
    color: '#fff',
    textAlign: 'left',
  },
  weatherIcon: {
    width: 80,
    height: 80,
    marginLeft: 10,
    marginTop: 20,
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
  loadingText: {
    color: '#fff',
    fontSize: 16,
    textAlign: 'center',
  },
  errorText: {
    color: '#fff',
    fontSize: 16,
    textAlign: 'center',
    marginTop: 35,
    marginRight: 15,
  },
});

export default CurrentLocation;
