import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  Dimensions,
  Switch,
  Alert,
  ActivityIndicator,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import LinearGradient from 'react-native-linear-gradient';
import {
  fetchUserLocation,
  fetchWeatherData,
  fetchWeatherTags,
} from '../api/api';

const {width, height} = Dimensions.get('window');

const WeatherHeader = ({accessToken, onToggleChange}) => {
  const [userLocation, setUserLocation] = useState({city: '', street: ''});
  const [weatherData, setWeatherData] = useState({});
  const [weatherTags, setWeatherTags] = useState([]);
  const [isToggled, setIsToggled] = useState(false);
  const [loading, setLoading] = useState(true);
  const [backgroundColors, setBackgroundColors] = useState([
    '#4e9cf5',
    '#498bf5',
    '#3f6be8',
    '#3564e8',
  ]);

  const isNightTime = () => {
    const currentHour = new Date().getHours();
    return currentHour >= 18 || currentHour < 6;
  };

  const isCloudyOrRainyCondition = () => {
    if (!weatherData || !weatherData.weatherPerHourList) return false;

    const currentHour = new Date().getHours();
    const dayConditions = weatherData.weatherPerHourList.filter(item => {
      const hour = new Date(item.hour).getHours();

      console.log(
        `Hour: ${hour}, SkyType: ${item.skyType}, Rain: ${item.rain}`,
      );

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
        updateBackgroundColors();

        const location = await fetchUserLocation(accessToken);
        const weather = await fetchWeatherData(accessToken);
        const tags = await fetchWeatherTags(accessToken);

        setUserLocation({
          city: location?.city || '',
          street: location?.street || '',
        });
        setWeatherData(weather?.result);
        setWeatherTags(tags);

        const storedState = await AsyncStorage.getItem('switchState');
        if (storedState !== null) {
          const parsedState = JSON.parse(storedState);
          setIsToggled(parsedState);
          onToggleChange(parsedState);
        }

        updateBackgroundColors(weather?.result);
        setLoading(false);
      } catch (error) {
        Alert.alert('Error', '데이터를 불러오는 데 실패했습니다.');
        setLoading(false);
      }
    };

    loadData();

    const interval = setInterval(() => {
      console.log('Refreshing weather data...');
      loadData();
    }, 60000);

    return () => clearInterval(interval);
  }, [accessToken, onToggleChange]);

  const updateBackgroundColors = (data = null) => {
    if (isNightTime()) {
      setBackgroundColors(['#405063', '#1D2837', '#161B2C']);
    } else if (data && isCloudyOrRainyCondition()) {
      setBackgroundColors(['#a2c8db', '#8BAEBF', '#7998a6']);
    } else {
      setBackgroundColors(['#4e9cf5', '#498bf5', '#3f6be8', '#3564e8']);
    }
  };

  const handleToggle = async () => {
    const newState = !isToggled;
    setIsToggled(newState);
    onToggleChange(newState);
    await AsyncStorage.setItem('switchState', JSON.stringify(newState));
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
      <LinearGradient
        colors={backgroundColors}
        start={{x: 0, y: 0}}
        end={{x: 1, y: 1}}
        style={styles.headerContainer}>
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#fff" />
        </View>
      </LinearGradient>
    );
  }

  return (
    <LinearGradient
      colors={backgroundColors}
      start={{x: 0, y: 0}}
      end={{x: 1, y: 1}}
      style={styles.headerContainer}>
      <Switch
        value={isToggled}
        onValueChange={handleToggle}
        style={styles.switch}
      />

      <View style={styles.infoContainer}>
        <View style={styles.locationContainer}>
          <Image
            source={require('../../assets/images/icon_location.png')}
            style={styles.locationIcon}
          />
          <Text style={styles.location}>
            {userLocation.city} {userLocation.street}
          </Text>
        </View>
        <Text style={styles.temperature}>{weatherData?.currentTmp}°C</Text>
        <Text style={styles.feelsLike}>
          체감 {weatherData?.currentSensibleTmp?.toFixed(2)}°C
        </Text>
      </View>

      <Image
        source={getWeatherIcon(weatherData?.currentSkyType)}
        style={styles.weatherIcon}
        resizeMode="contain"
      />

      <View style={styles.tagsContainer}>
        {weatherTags.map((tag, index) => (
          <View key={index} style={styles.tag}>
            <Text style={styles.tagText}>{tag.text}</Text>
          </View>
        ))}
      </View>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  headerContainer: {
    width: width,
    height: height * 0.23,
    paddingVertical: 20,
    paddingHorizontal: 20,
    paddingLeft: width * 0.07,
    borderBottomLeftRadius: 20,
    borderBottomRightRadius: 20,
  },
  switch: {
    marginTop: height * 0.03,
    position: 'absolute',
    top: width * 0.05,
    right: width * 0.07,
    zIndex: 1,
  },
  infoContainer: {
    marginTop: height * 0.03,
    marginBottom: 40,
  },
  locationContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: Platform.OS === 'ios' ? 5 : 1,
  },
  locationIcon: {
    tintColor: '#fff',
    width: width * 0.05,
    height: width * 0.05,
    marginRight: 5,
  },
  location: {
    color: '#fff',
    fontSize: width * 0.04,
  },
  temperature: {
    color: '#fff',
    fontSize: width * 0.09,
    fontWeight: 'bold',
    marginTop: 5,
  },
  feelsLike: {
    color: '#fff',
    fontSize: width * 0.04,
    marginTop: Platform.OS === 'ios' ? 5 : -3,
  },
  weatherIcon: {
    position: 'absolute',
    bottom: height * 0.04,
    right: width * 0.05,
    width: width * 0.22,
    height: width * 0.22,
  },
  tagsContainer: {
    position: 'absolute',
    bottom: 10,
    left: 20,
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  tag: {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 20,
    paddingVertical: 5,
    paddingHorizontal: 12,
    marginRight: 10,
    marginBottom: 5,
  },
  tagText: {
    color: '#fff',
    fontSize: width * 0.032,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default WeatherHeader;
