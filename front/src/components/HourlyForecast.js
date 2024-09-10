import React, {useEffect, useState} from 'react';
import {ScrollView, Text, View, StyleSheet, Image} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import {fetchWeatherData} from '../api/api';

const HourlyForecast = ({accessToken, memberId, showText}) => {
  const [hourlyData, setHourlyData] = useState([]);

  useEffect(() => {
    const getWeatherData = async () => {
      try {
        const weatherData = await fetchWeatherData(memberId, accessToken);

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
    paddingHorizontal: 5,
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 10,
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
    width: 30,
    height: 30,
    marginBottom: 5,
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
