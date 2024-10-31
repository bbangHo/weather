import React, {useEffect, useState} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  Image,
  Platform,
} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import {fetchWeatherData} from '../api/api';

const HourlyForecast = ({accessToken, showText}) => {
  const [hourlyData, setHourlyData] = useState([]);

  useEffect(() => {
    const getWeatherData = async () => {
      try {
        const weatherData = await fetchWeatherData(accessToken);

        console.log('Backend response:', JSON.stringify(weatherData, null, 2));

        if (weatherData.isSuccess) {
          setHourlyData(weatherData.result.weatherPerHourList);
        } else {
          console.error('Failed to fetch weather data:', weatherData.message);
        }
      } catch (error) {
        console.error('Error fetching weather data:', error.message);
      }
    };

    getWeatherData();
  }, [accessToken]);

  const formatHour = isoString => {
    const date = new Date(isoString);
    const hours = date.getHours().toString().padStart(2, '0');
    return `${hours}시`;
  };

  const getWeatherIcon = skyType => {
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
  };

  return (
    <ScrollView horizontal style={styles.container}>
      {hourlyData.map((item, i) => (
        <Card
          key={i}
          containerStyle={[styles.card, globalStyles.transparentBackground]}>
          <View style={styles.content}>
            <Text style={styles.textTime}>{formatHour(item.hour)}</Text>

            <Image source={getWeatherIcon(item.skyType)} style={styles.icon} />

            {showText && item.rainAdverb ? (
              <Text style={styles.rainAdverb}>{item.rainAdverb}</Text>
            ) : (
              <View
                style={showText ? styles.adverbPlaceholder : styles.noAdverb}
              />
            )}

            <Text style={styles.rainText}>
              {showText ? item.rainText : `${item.rain}mm`}
            </Text>

            {showText && item.tmpAdverb ? (
              <Text style={styles.adverbText}>{item.tmpAdverb}</Text>
            ) : (
              <View
                style={showText ? styles.adverbPlaceholder : styles.noAdverb}
              />
            )}

            <Text style={styles.tmpText}>
              {showText ? item.tmpText : `${item.tmp}°C`}
            </Text>
          </View>
        </Card>
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    marginVertical: 5,
    paddingHorizontal: Platform.select({
      ios: 8,
      android: 9,
    }),
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: Platform.OS === 'ios' ? 9.5 : 12,
    paddingVertical: 20,
    marginHorizontal: 5,
    alignItems: 'center',
  },
  content: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  textTime: {
    color: '#fff',
    fontSize: 13,
    marginBottom: 5,
    textAlign: 'center',
  },
  icon: {
    width: 40,
    height: 40,
    marginBottom: 0,
  },
  rainAdverb: {
    color: 'skyblue',
    fontSize: 12,
    marginTop: 5,
    marginBottom: 0,
    textAlign: 'center',
  },
  adverbText: {
    color: '#fff',
    fontSize: 12,
    marginTop: 5,
    marginBottom: 0,
    textAlign: 'center',
  },
  rainText: {
    color: 'skyblue',
    fontSize: 14,
    marginVertical: 4,
    textAlign: 'center',
    marginBottom: 5,
  },
  tmpText: {
    color: '#fff',
    fontSize: 15,
    marginVertical: 4,
    textAlign: 'center',
  },
  adverbPlaceholder: {
    height: 14,
  },
  noAdverb: {
    height: 0,
  },
});

export default HourlyForecast;
