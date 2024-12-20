import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  Dimensions,
  ActivityIndicator,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {fetchUserLocation, fetchWeatherData} from '../api/api';

const {width, height} = Dimensions.get('window');

const WeatherHeaderCommunity = ({accessToken}) => {
  const [userLocation, setUserLocation] = useState({city: '', street: ''});
  const [weatherData, setWeatherData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [backgroundColors, setBackgroundColors] = useState([]);

  const isNightTime = () => {
    const currentHour = new Date().getHours();
    return currentHour >= 18 || currentHour < 6;
  };

  const isCloudyOrRainyCondition = () => {
    if (!weatherData || !weatherData.weatherPerHourList) return false;

    const currentHour = new Date().getHours();
    const dayConditions = weatherData.weatherPerHourList.filter(item => {
      const hour = new Date(item.hour).getHours();

      if (hour > currentHour) return false;

      return (
        hour >= 6 && hour < 18 && (item.skyType === 'CLOUDY' || item.rain > 0)
      );
    });

    return dayConditions.length > 0;
  };

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

        updateBackgroundColors(weather?.result);
      } catch (error) {
        console.error('Error fetching data:', error.message);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [accessToken]);

  useEffect(() => {
    const interval = setInterval(() => {
      updateBackgroundColors(weatherData);
    }, 60000);

    return () => clearInterval(interval);
  }, [weatherData]);

  const updateBackgroundColors = data => {
    if (isNightTime()) {
      setBackgroundColors(['#2e3947', '#1D2837', '#161B2C']);
    } else if (isCloudyOrRainyCondition()) {
      setBackgroundColors(['#a2c8db', '#8BAEBF', '#7998a6']);
    } else {
      setBackgroundColors(['#4e9cf5', '#498bf5', '#3f6be8', '#3564e8']);
    }
  };

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
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#fff" />
      </View>
    );
  }

  return (
    <LinearGradient
      colors={backgroundColors}
      start={{x: 0, y: 0}}
      end={{x: 1, y: 1}}
      style={styles.headerContainer}>
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
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  headerContainer: {
    width: width,
    height: height * 0.15,
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
    marginTop: height * 0.02,
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
