import React, {useEffect, useState} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  Image,
  Dimensions,
  ActivityIndicator,
  Platform,
} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import {fetchWeatherData} from '../api/api';

const {width, height} = Dimensions.get('window');

const aspectRatio = height / width;

const HourlyForecast = ({accessToken, showText, refreshing}) => {
  const [hourlyData, setHourlyData] = useState([]);
  const [loading, setLoading] = useState(true);

  const getWeatherData = async () => {
    try {
      const weatherData = await fetchWeatherData(accessToken);
      console.log('Hourly Weather data:', JSON.stringify(weatherData, null, 2));
      console.log('width / height = ', width, '/', height, '=', aspectRatio);

      if (weatherData.isSuccess && weatherData.result?.weatherPerHourList) {
        setHourlyData(weatherData.result.weatherPerHourList);
      } else {
        console.error('Failed to fetch weather data:', weatherData.message);
        setHourlyData([]);
      }
    } catch (error) {
      console.error('Error fetching weather data:', error.message);
      setHourlyData([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getWeatherData();

    const interval = setInterval(() => {
      console.log('Updating hourly weather data...');
      getWeatherData();
    }, 300000);

    return () => clearInterval(interval);
  }, [accessToken, refreshing]);

  const formatHour = isoString => {
    const date = new Date(isoString);
    const hours = date.getHours().toString().padStart(2, '0');
    return `${hours}시`;
  };

  const isNightTime = hour => {
    return hour >= 18 || hour < 6;
  };

  const getWeatherIcon = (skyType, rain, hour) => {
    if (rain > 0) {
      return require('../../assets/images/icon_rain.png');
    }

    const isNight = isNightTime(hour);

    if (isNight) {
      switch (skyType) {
        case 'CLEAR':
          return require('../../assets/images/icon_clearNight.png');
        case 'PARTLYCLOUDY':
          return require('../../assets/images/icon_partlycloudyNight.png');
        case 'CLOUDY':
          return require('../../assets/images/icon_cloudyNight.png');
        default:
          return require('../../assets/images/icon_default.png');
      }
    } else {
      switch (skyType) {
        case 'CLEAR':
          return require('../../assets/images/icon_clear.png');
        case 'PARTLYCLOUDY':
          return require('../../assets/images/icon_partlycloudy.png');
        case 'CLOUDY':
          return require('../../assets/images/icon_cloudy.png');
        default:
          return require('../../assets/images/icon_default.png');
      }
    }
  };

  const placeholderData = Array(8).fill({});

  return (
    <ScrollView
      horizontal
      contentContainerStyle={styles.scrollContent}
      style={[
        styles.container,
        aspectRatio === 2.09999 && {marginTop: -width * 0.13},
      ]}>
      {(loading ? placeholderData : hourlyData || []).map((item, i) => (
        <View key={i} style={styles.shadowContainer}>
          <Card
            containerStyle={[styles.card, globalStyles.transparentBackground]}>
            <View style={styles.content}>
              {loading ? (
                <ActivityIndicator size="small" color="#999999" />
              ) : item ? (
                <>
                  <Text style={styles.textTime}>{formatHour(item.hour)}</Text>

                  <Image
                    source={getWeatherIcon(
                      item.skyType,
                      item.rain,
                      new Date(item.hour).getHours(),
                    )}
                    style={styles.icon}
                  />

                  <View style={styles.placeholder}>
                    {showText && item.tmpAdverb ? (
                      <Text style={styles.adverbText}>{item.tmpAdverb}</Text>
                    ) : null}
                  </View>

                  <Text style={styles.tmpText}>
                    {showText ? item.tmpText : `${item.tmp}°C`}
                  </Text>

                  <View style={styles.placeholder}>
                    {showText && item.rainAdverb ? (
                      <Text style={styles.rainAdverb}>{item.rainAdverb}</Text>
                    ) : null}
                  </View>

                  <Text style={styles.rainText}>
                    {showText ? item.rainText : `${item.rain}mm`}
                  </Text>
                </>
              ) : null}
            </View>
          </Card>
        </View>
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    marginTop: Math.abs(aspectRatio) < 2.1 ? -width * 0.13 : -width * 0.01,
    paddingRight: 10,
    marginRight: 10,
    marginTop: 0,
  },
  shadowContainer: {
    marginLeft: width * 0.025,
    marginVertical: 10,
    backgroundColor: '#fff',
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: {width: 1, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  card: {
    borderRadius: 12,
    borderColor: '#fff',
    padding: -30,
    width: width * 0.13,
    height: Platform.OS === 'ios' ? 140 : 150,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#fff',
  },
  content: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  placeholder: {
    height: 12,
    justifyContent: 'center',
  },
  textTime: {
    fontSize: 14,
    color: '#333',
    marginBottom: -10,
    fontWeight: '600',
    textAlign: 'center',
    marginTop: -20,
  },
  icon: {
    width: 45,
    height: 45,
    marginBottom: -5,
    marginTop: 10,
  },
  rainAdverb: {
    fontSize: 11,
    marginBottom: -13,
    textAlign: 'center',
  },
  adverbText: {
    fontSize: 12,
    marginBottom: Platform.OS === 'ios' ? -5 : -8,
    textAlign: 'center',
    marginTop: 5,
  },
  tmpText: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#000',
    marginBottom: -4,
    marginTop: 5,
    textAlign: 'center',
  },
  rainText: {
    fontSize: 13,
    color: '#555',
    textAlign: 'center',
    marginTop: 5,
    marginBottom: -5,
  },
});

export default HourlyForecast;
