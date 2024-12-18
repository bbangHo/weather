import React, {useEffect, useState} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  Image,
  Dimensions,
} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import {fetchWeatherData} from '../api/api';

const {width} = Dimensions.get('window');

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

  const getWeatherIcon = (skyType, rain) => {
    if (rain > 0) {
      return require('../../assets/images/icon_rain.png');
    }
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
    <ScrollView
      horizontal
      contentContainerStyle={styles.scrollContent}
      style={styles.container}>
      {hourlyData.map((item, i) => (
        <Card
          key={i}
          containerStyle={[styles.card, globalStyles.transparentBackground]}>
          <View style={styles.content}>
            <Text style={styles.textTime}>{formatHour(item.hour)}</Text>

            <Image
              source={getWeatherIcon(item.skyType, item.rain)}
              style={styles.icon}
            />

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
          </View>
        </Card>
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    marginTop: -width * 0.16,
  },

  card: {
    borderRadius: 12,
    paddingVertical: 15,
    width: width * 0.22,
    height: 150,
    marginRight: -width * 0.01,
    marginLeft: width * 0.025,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.1,
    elevation: 2,
  },
  content: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  textTime: {
    fontSize: 14,
    color: '#333',
    marginBottom: 5,
    fontWeight: '600',
    textAlign: 'center',
  },
  icon: {
    width: 40,
    height: 40,
    marginBottom: 5,
  },
  rainAdverb: {
    fontSize: 12,

    marginBottom: 0,
    textAlign: 'center',
  },
  adverbText: {
    fontSize: 12,
    marginBottom: 1,
    textAlign: 'center',
  },
  tmpText: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#000',
    marginBottom: 7,
    textAlign: 'center',
  },
  rainText: {
    fontSize: 13,
    color: '#555',
    textAlign: 'center',
  },
});

export default HourlyForecast;
