import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Alert,
  Image,
  Dimensions,
  Platform,
  ActivityIndicator,
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

  const windowWidth = Dimensions.get('window').width;
  const marginLeftValue = Platform.select({
    ios: windowWidth * 0.07,
    android: windowWidth * 0.09,
  });

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#fff" />
      </View>
    );
  }

  return (
    <View style={[styles.container, {marginLeft: marginLeftValue}]}>
      {userLocation && weatherData ? (
        <View>
          <View style={styles.locationContainer}>
            <Text style={styles.location}>{userLocation.city}</Text>
            <Image
              source={getWeatherIcon(weatherData.currentSkyType)}
              style={styles.weatherIcon}
            />
          </View>
          <Text style={styles.street}>{userLocation.street}</Text>
          <Text style={styles.temperature}>
            {weatherData.currentTmp !== null
              ? `${weatherData.currentTmp}°C`
              : 'Loading...'}
          </Text>
        </View>
      ) : (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#fff" />
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'flex-start',
    justifyContent: 'center',
    borderRadius: 10,
    marginTop: -20,
    flexDirection: 'column',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  locationContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: -5,
  },
  location: {
    fontSize: 20,
    color: '#fff',
    textAlign: 'left',
  },
  street: {
    fontSize: 20,
    color: '#fff',
    marginTop: -20,
  },
  weatherIcon: {
    width: 80,
    height: 80,
    marginLeft: 10,
    marginTop: 20,
  },
  temperature: {
    fontSize: 22,
    color: '#fff',
    marginTop: 17,
  },
});

export default CurrentLocation;
