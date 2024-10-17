import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Dimensions} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import globalStyles from '../globalStyles';
import {fetchExtraWeatherInfo} from '../api/api';

const AirQuality = ({accessToken}) => {
  const [extraWeatherInfo, setExtraWeatherInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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

  useEffect(() => {
    const loadExtraWeatherInfo = async () => {
      try {
        const data = await fetchExtraWeatherInfo(accessToken);
        console.log('fetch extra weather info:', data);
        setExtraWeatherInfo(data);
        setLoading(false);
      } catch (error) {
        setError(error.message);
        setLoading(false);
      }
    };

    if (accessToken) {
      loadExtraWeatherInfo();
    }
  }, [accessToken]);

  if (loading) {
    return <Text>데이터를 불러오는 중...</Text>;
  }

  if (error) {
    return <Text>오류: {error}</Text>;
  }

  return (
    <View style={styles.container}>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>초미세먼지</Text>
        <Text style={styles.value}>
          {getGradeText(extraWeatherInfo.pm25Grade)}
        </Text>
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>미세먼지</Text>
        <Text style={styles.value}>
          {getGradeText(extraWeatherInfo.pm10Grade)}
        </Text>
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>자외선</Text>
        <Text style={styles.value}>
          {getGradeText(extraWeatherInfo.uvGrade)}
        </Text>
      </View>
      <View style={[styles.box, globalStyles.transparentBackground]}>
        <Text style={styles.title}>오존</Text>
        <Text style={styles.value}>
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
    paddingHorizontal: 5,
    marginVertical: 5,
    marginBottom: -5,
    padding: 9,
    borderRadius: 10,
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
    color: '#fff',
    marginBottom: 5,
    marginTop: 5,
  },
  data: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
  },
});

export default AirQuality;
