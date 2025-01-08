import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  Dimensions,
  Switch,
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
  const [weatherData, setWeatherData] = useState(null);
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
    return weatherData.weatherPerHourList.some(item => {
      const hour = new Date(item.hour).getHours();

      console.log(
        `Hour: ${hour}, SkyType: ${item.skyType}, Rain: ${item.rain}`,
      );
      return (
        hour <= currentHour &&
        hour >= 6 &&
        hour < 18 &&
        (item.skyType === 'CLOUDY' || item.rain > 0)
      );
    });
  };

  useEffect(() => {
    const loadData = async () => {
      try {
        console.log('Loading header data...');
        const location = await fetchUserLocation(accessToken);
        const weather = await fetchWeatherData(accessToken);
        const tags = await fetchWeatherTags(accessToken);

        setUserLocation({
          city: location?.city || '',
          street: location?.street || '',
        });
        setWeatherData(weather?.result || null);
        setWeatherTags(Array.isArray(tags) ? tags : []);

        const storedState = await AsyncStorage.getItem('switchState');
        if (storedState !== null) {
          const parsedState = JSON.parse(storedState);
          setIsToggled(parsedState);
          onToggleChange(parsedState);
        }
      } catch (error) {
        console.error('Error fetching data:', error.message);
      } finally {
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

  useEffect(() => {
    if (weatherData) {
      updateBackgroundColors(weatherData);
    }
  }, [weatherData]);

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

  const getWeatherIcon = (currentSkyType, rain) => {
    const currentHour = new Date().getHours();
    const isNight = currentHour >= 18 || currentHour < 6;

    if (rain > 0) {
      return isNight
        ? require('../../assets/images/icon_weather_rainNight.png')
        : require('../../assets/images/icon_weather_rain.png');
    }

    if (isNight) {
      switch (currentSkyType) {
        case 'CLEAR':
          return require('../../assets/images/icon_weather_clearNight.png');
        case 'PARTLYCLOUDY':
          return require('../../assets/images/icon_weather_partlycloudyNight.png');
        case 'CLOUDY':
          return require('../../assets/images/icon_weather_partlycloudyNight.png');
        default:
          return require('../../assets/images/icon_default.png');
      }
    } else {
      switch (currentSkyType) {
        case 'CLEAR':
          return require('../../assets/images/icon_weather_clear.png');
        case 'PARTLYCLOUDY':
          return require('../../assets/images/icon_weather_partlycloudy.png');
        case 'CLOUDY':
          return require('../../assets/images/icon_weather_cloudy.png');
        default:
          return require('../../assets/images/icon_default.png');
      }
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
          체감 {weatherData?.currentSensibleTmp?.toFixed(1)}°C
        </Text>
      </View>

      <Image
        source={getWeatherIcon(
          weatherData?.currentSkyType,
          weatherData?.rain || 0,
        )}
        style={styles.weatherIcon}
        resizeMode="contain"
      />

      <View style={styles.tagsContainer}>
        {weatherTags.length > 0 ? (
          weatherTags.map((tag, index) => (
            <View key={index} style={styles.tag}>
              <Text
                style={styles.tagText}
                numberOfLines={1}
                ellipsizeMode="tail">
                {tag.text}
              </Text>
            </View>
          ))
        ) : (
          <View style={styles.tag}>
            <Text style={styles.tagText}>
              게시글을 작성해서 태그를 공유해 주세요!
            </Text>
          </View>
        )}
      </View>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  headerContainer: {
    width: width,
    height: Platform.OS === 'ios' ? height * 0.23 : height * 0.2,
    paddingVertical: 20,
    paddingHorizontal: 20,
    paddingLeft: width * 0.07,
    borderBottomLeftRadius: 20,
    borderBottomRightRadius: 20,
  },
  switch: {
    marginTop: Platform.OS === 'ios' ? height * 0.03 : height * 0.003,
    position: 'absolute',
    top: width * 0.05,
    right: width * 0.07,
    zIndex: 1,
  },
  infoContainer: {
    marginTop: Platform.OS === 'ios' ? height * 0.03 : height * 0.002,
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
    flexWrap: 'nowrap',
  },
  tag: {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 20,
    paddingVertical: 3,
    paddingHorizontal: 12,
    marginRight: 8,
    marginBottom: 5,
    maxWidth: width * 0.4,
  },
  tagText: {
    color: '#fff',
    fontSize: width * 0.031,
    paddingBottom: Platform.OS === 'ios' ? 0 : 3,
    overflow: 'hidden',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default WeatherHeader;
