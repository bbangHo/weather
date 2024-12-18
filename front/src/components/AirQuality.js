import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Dimensions} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {fetchExtraWeatherInfo} from '../api/api';

const {width} = Dimensions.get('window');

const AirQuality = ({accessToken}) => {
  const [extraWeatherInfo, setExtraWeatherInfo] = useState({
    pm25Grade: 0,
    pm10Grade: 0,
    uvGrade: 0,
    o3Grade: 0,
  });
  const [loading, setLoading] = useState(true);

  const getGradeText = grade => {
    switch (grade) {
      case 1:
        return '좋음';
      case 2:
        return '보통';
      case 3:
        return '나쁨';
      case 4:
        return '매우 나쁨';
      default:
        return '정보 없음';
    }
  };

  const getGradientColors = grade => {
    switch (grade) {
      case 1:
        return ['#EFF6FF', '#DBEAFE'];
      case 2:
        return ['#F0FDF4', '#DCFCE7'];
      case 3:
        return ['#FFF7ED', '#FFEDD5'];
      case 4:
        return ['#FEF2F2', '#FEE2E2'];
      default:
        return ['#F4F4F5', '#E4E4E7'];
    }
  };

  useEffect(() => {
    const loadExtraWeatherInfo = async () => {
      try {
        const data = await fetchExtraWeatherInfo(accessToken);
        console.log('fetched extra weather info:', data);
        setExtraWeatherInfo(data);
      } catch (error) {
        console.error('Error fetching extra weather info:', error);
      } finally {
        setLoading(false);
      }
    };

    if (accessToken) {
      loadExtraWeatherInfo();
    }
  }, [accessToken]);

  return (
    <View style={styles.container}>
      <View style={styles.row}>
        {['pm10Grade', 'pm25Grade'].map((key, index) => (
          <LinearGradient
            key={index}
            colors={getGradientColors(extraWeatherInfo[key])}
            style={styles.box}>
            <Text style={styles.title}>
              {key === 'pm25Grade' ? '초미세먼지' : '미세먼지'}
            </Text>
            <Text style={styles.value}>
              {getGradeText(extraWeatherInfo[key])}
            </Text>
          </LinearGradient>
        ))}
      </View>
      <View style={styles.row}>
        {['uvGrade', 'o3Grade'].map((key, index) => (
          <LinearGradient
            key={index}
            colors={getGradientColors(extraWeatherInfo[key])}
            style={styles.box}>
            <Text style={styles.title}>
              {key === 'uvGrade' ? '자외선' : '오존'}
            </Text>
            <Text style={styles.value}>
              {getGradeText(extraWeatherInfo[key])}
            </Text>
          </LinearGradient>
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'column',
    justifyContent: 'center',
    marginTop: 10,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
    paddingHorizontal: 10,
  },
  box: {
    width: width * 0.45,
    height: 77,
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 2,
  },
  title: {
    fontSize: 13,
    color: '#333',
    marginBottom: 5,
  },
  value: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  unitText: {
    fontSize: 12,
    color: '#666',
    marginTop: 2,
  },
});

export default AirQuality;
