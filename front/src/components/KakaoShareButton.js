import React, {useState, useEffect} from 'react';
import {View, StyleSheet, Alert} from 'react-native';
import {Button, Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import KakaoShareLink from 'react-native-kakao-share-link';
import {fetchWeatherData, fetchRainForecast} from '../api/api';

const KakaoShareButton = ({accessToken}) => {
  const [weatherInfo, setWeatherInfo] = useState(null);
  const [rainComment, setRainComment] = useState('');

  useEffect(() => {
    const fetchWeather = async () => {
      try {
        const data = await fetchWeatherData(accessToken);
        if (data.result) {
          setWeatherInfo(data.result);
        }
      } catch (error) {
        console.error('날씨 정보 가져오기 오류:', error);
      }
    };

    const fetchRain = async () => {
      try {
        const rainData = await fetchRainForecast(accessToken);
        console.log('Fetched rain forecast:', rainData);
        setRainComment(rainData.rainComment || '');
      } catch (error) {
        console.error('비 예보 정보 가져오기 오류:', error);
      }
    };

    fetchWeather();
    fetchRain();
  }, [accessToken]);

  const getWeatherEmoji = skyType => {
    switch (skyType) {
      case 'CLEAR':
        return '☀️';
      case 'PARTLYCLOUDY':
        return '⛅️';
      case 'CLOUDY':
        return '☁️';
      default:
        return '🌈';
    }
  };

  const shareWeatherInfo = async () => {
    if (!weatherInfo) {
      Alert.alert(
        '오류',
        '날씨 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.',
      );
      return;
    }

    const {city, street, currentTmp, currentSkyType, temperature} = weatherInfo;
    const {maxTmp, minTmp} = temperature;
    const weatherEmoji = getWeatherEmoji(currentSkyType);

    try {
      const response = await KakaoShareLink.sendFeed({
        content: {
          title: `${city} ${street} 날씨입니다 ~`,
          description: `현재 ${currentTmp}°C\n${rainComment}\n최고 ${maxTmp}°, 최저 ${minTmp}°`,
          imageUrl: 'https:이미지 추가할 경우.png',
          link: {
            mobileWebUrl: 'https://링크 추가.com',
            webUrl: 'https://링크 추가.com',
          },
        },
        buttons: [
          {
            title: '모바일 앱에서 확인해 보세요.',
            link: {
              mobileWebUrl: 'https://링크 추가.com',
              webUrl: 'https://링크 추가.com',
            },
          },
        ],
      });
      if (response.success) {
        Alert.alert('성공', '카카오톡으로 공유되었습니다.');
      }
    } catch (error) {
      Alert.alert('오류', '카카오톡 공유에 실패했습니다.');
      console.error('카카오 공유 오류:', error);
    }
  };

  return (
    <View style={styles.container}>
      <Card containerStyle={[globalStyles.transparentBackground, styles.card]}>
        <Button
          title="카카오톡으로 날씨 공유하기"
          onPress={shareWeatherInfo}
          buttonStyle={styles.button}
          titleStyle={styles.buttonTitle}
        />
      </Card>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'center',
    paddingHorizontal: 10,
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 0,
    paddingHorizontal: 10,
    paddingVertical: Platform.OS === 'ios' ? 1 : 3,
  },
  button: {
    backgroundColor: 'transparent',
    paddingHorizontal: 10,
  },
  buttonTitle: {
    fontSize: 14,
  },
});

export default KakaoShareButton;
