import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Dimensions} from 'react-native';
import globalStyles from '../globalStyles';
import {fetchExtraWeatherInfo} from '../api/api';

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

  const getGradeColor = grade => {
    switch (grade) {
      case 1:
        return '#81BEF7';
      case 2:
        return '#A9F5A9';
      case 3:
        return '#F78181';
      case 4:
        return '#F78181';
      default:
        return '#c4c4c4';
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
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>초미세먼지</Text>
        <Text
          style={[
            styles.value,
            {color: getGradeColor(extraWeatherInfo.pm25Grade)},
          ]}>
          {getGradeText(extraWeatherInfo.pm25Grade)}
        </Text>
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>미세먼지</Text>
        <Text
          style={[
            styles.value,
            {color: getGradeColor(extraWeatherInfo.pm10Grade)},
          ]}>
          {getGradeText(extraWeatherInfo.pm10Grade)}
        </Text>
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>자외선</Text>
        <Text
          style={[
            styles.value,
            {color: getGradeColor(extraWeatherInfo.uvGrade)},
          ]}>
          {getGradeText(extraWeatherInfo.uvGrade)}
        </Text>
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>오존</Text>
        <Text
          style={[
            styles.value,
            {color: getGradeColor(extraWeatherInfo.o3Grade)},
          ]}>
          {getGradeText(extraWeatherInfo.o3Grade)}
        </Text>
      </View>
    </View>
  );
};

const {width} = Dimensions.get('window');

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 2,
    marginHorizontal: 5,
    marginVertical: 5,
    marginBottom: -5,
    padding: 9,
    borderRadius: 10,
  },
  loadingText: {
    fontSize: 16,
    color: '#fff',
    textAlign: 'center',
    marginTop: 20,
  },
  errorText: {
    fontSize: 16,
    color: '#fff',
    textAlign: 'center',
    marginTop: 20,
  },
  box: {
    borderRadius: 10,
    padding: 10,
    paddingVertical: 15,
    alignItems: 'center',
    margin: 3,
    width: '23%',
  },
  title: {
    fontSize: 13,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 8,
  },
  value: {
    fontSize: 14,
    marginBottom: 5,
    marginTop: 5,
  },
});

export default AirQuality;
