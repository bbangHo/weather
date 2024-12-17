import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  Dimensions,
  ActivityIndicator,
} from 'react-native';
import {fetchUserLocation, fetchWeatherData} from '../api/api';

const {width, height} = Dimensions.get('window');

const WeatherHeaderCommunity = ({accessToken}) => {
  const [userLocation, setUserLocation] = useState('');
  const [weatherData, setWeatherData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadData = async () => {
      try {
        const location = await fetchUserLocation(accessToken);
        const weather = await fetchWeatherData(accessToken);

        setUserLocation({
          city: location?.city || '',
          street: location?.street || '',
        });
        setWeatherData(weather?.result);
      } catch (error) {
        console.error('Error fetching data:', error.message);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [accessToken]);

  const getWeatherIcon = currentSkyType => {
    switch (currentSkyType) {
      case 'CLEAR':
        return require('../../assets/images/icon_clear.png');
      case 'PARTLYCLOUDY':
        return require('../../assets/images/icon_partlycloudy.png');
      default:
        return require('../../assets/images/icon_cloudy.png');
    }
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#fff" />
      </View>
    );
  }

  return (
    <View style={styles.headerContainer}>
      <View style={styles.textContainer}>
        <Text style={styles.location}>
          {userLocation.city} {userLocation.street}
        </Text>
        <Text style={styles.temperature}>{weatherData?.currentTmp}°C</Text>
      </View>
      <View style={styles.iconContainer}>
        <Image
          source={getWeatherIcon(weatherData?.currentSkyType)}
          style={styles.weatherIcon}
          resizeMode="contain"
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  headerContainer: {
    width: width,
    height: height * 0.15,
    backgroundColor: '#3f7dfd',
    borderBottomLeftRadius: 20,
    borderBottomRightRadius: 20,
    paddingLeft: width * 0.07,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  textContainer: {
    flexDirection: 'column',
    justifyContent: 'center',
  },
  location: {
    marginTop: height * 0.03,
    color: '#fff',
    fontSize: width * 0.04,
    marginBottom: 5,
  },
  temperature: {
    color: '#fff',
    fontSize: width * 0.09,
    fontWeight: 'bold',
  },
  iconContainer: {
    width: width * 0.25,
    height: width * 0.25,
    justifyContent: 'center',
    alignItems: 'center',
  },
  weatherIcon: {
    width: '100%',
    height: '100%',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default WeatherHeaderCommunity;
