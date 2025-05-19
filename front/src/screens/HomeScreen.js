import React, {useState, useCallback, useEffect} from 'react';
import {useRefresh} from '../contexts/RefreshContext';
import {
  ScrollView,
  View,
  StatusBar,
  RefreshControl,
  TouchableOpacity,
  StyleSheet,
  Image,
  Dimensions,
} from 'react-native';
import {useFocusEffect} from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {Alert} from 'react-native';
import WeatherHeader from '../components/WeatherHeader';
import HourlyForecast from '../components/HourlyForecast';
import AirQuality from '../components/AirQuality';
import WeatherGraph from '../components/WeatherGraph';
import Posts from '../components/Posts';
import KakaoShareButton from '../components/KakaoShareButton';
import globalStyles from '../globalStyles';
import {useCopilot, CopilotStep, walkthroughable} from 'react-native-copilot';
import {InteractionManager} from 'react-native';
import {fetchWeatherData, checkInAttendance, fetchMemberInfo} from '../api/api';

const {width, height} = Dimensions.get('window');
const CopilotView = walkthroughable(View);
const TUTORIAL_COMPLETED_KEY = 'homeTutorialCompleted';

const HomeScreen = ({accessToken, navigation}) => {
  const {start} = useCopilot();

  const {refresh, setRefresh} = useRefresh();
  const [weatherData, setWeatherData] = useState(null);
  const [showText, setShowText] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [buttonBackgroundColor, setButtonBackgroundColor] = useState('#3f7dfd');

  const fetchWeather = async () => {
    try {
      const data = await fetchWeatherData(accessToken);

      if (data?.result?.weatherPerHourList) {
        const uniqueWeatherList = [];
        const seenHours = new Set();

        for (const item of data.result.weatherPerHourList) {
          const itemHour = new Date(item.hour).getHours();
          if (!seenHours.has(itemHour)) {
            uniqueWeatherList.push(item);
            seenHours.add(itemHour);
          }
        }

        setWeatherData({
          ...data.result,
          weatherPerHourList: uniqueWeatherList,
        });

        updateButtonBackgroundColor(uniqueWeatherList);
      } else {
        setWeatherData(data.result);
      }
    } catch (error) {
      console.error('Error fetching weather data:', error);
    }
  };

  const isNightTime = () => {
    const currentHour = new Date().getHours();
    return currentHour >= 18 || currentHour < 6;
  };

  const updateButtonBackgroundColor = weatherPerHourList => {
    if (!weatherPerHourList || weatherPerHourList.length === 0) {
      setButtonBackgroundColor(isNightTime() ? '#1D2837' : '#3f7dfd');
      return;
    }

    const currentHour = new Date().getHours();
    const closestWeather = weatherPerHourList.reduce((closest, item) => {
      const itemHour = new Date(item.hour).getHours();
      if (
        !closest ||
        Math.abs(itemHour - currentHour) <
          Math.abs(new Date(closest.hour).getHours() - currentHour)
      ) {
        return item;
      }
      return closest;
    }, null);

    if (isNightTime()) {
      setButtonBackgroundColor('#1D2837');
    } else if (
      closestWeather?.rain > 0 ||
      closestWeather?.skyType === 'CLOUDY'
    ) {
      setButtonBackgroundColor('#7998a6');
    } else {
      setButtonBackgroundColor('#3f7dfd');
    }
  };

  // 출석 체크 로직
  const checkAttendanceOncePerDay = async () => {
    const today = getKstToday(); // 한국 시간 기준 오늘 날짜 사용

    try {
      const memberInfo = await fetchMemberInfo(accessToken);
      console.log('Fetched member info:', memberInfo);

      const email = memberInfo?.result?.email;
      if (!email) {
        console.warn('이메일 정보를 가져오지 못했습니다.');
        return;
      }

      const attendanceKey = `lastAttendanceDate_${email}`;
      const lastCheckInDate = await AsyncStorage.getItem(attendanceKey);

      if (lastCheckInDate === today) {
        console.log(`이미 ${email} 계정으로 출석 완료된 날짜입니다:`, today);
        return;
      }

      const result = await checkInAttendance(accessToken);
      if (result?.isSuccess) {
        console.log('출석 체크 성공:', result);
        await AsyncStorage.setItem(attendanceKey, today);
        Alert.alert('출석 완료', '오늘도 출석했습니다!');
      } else {
        throw new Error(result?.message || '출석 실패');
      }
    } catch (error) {
      console.error('출석 체크 실패:', error.message);
      Alert.alert('출석 실패', error.message || '오류가 발생했습니다.');
    }
  };

  const getKstToday = () => {
    const now = new Date();
    const offsetMs = 9 * 60 * 60 * 1000;
    const kstNow = new Date(now.getTime() + offsetMs);
    return kstNow.toISOString().split('T')[0];
  };

  // 튜토리얼 시작 조건 및 실행
  useEffect(() => {
    const tryStartTutorial = async () => {
      const done = await AsyncStorage.getItem(TUTORIAL_COMPLETED_KEY);
      if (!done && !hasStartedTutorial) {
        setHasStartedTutorial(true); // 중복 방지
        InteractionManager.runAfterInteractions(() => {
          start();
          AsyncStorage.setItem(TUTORIAL_COMPLETED_KEY, 'true');
        });
      }
    };
    tryStartTutorial();
  }, [start]);

  useFocusEffect(
    useCallback(() => {
      fetchWeather(); // 날씨 데이터

      const tryStartTutorial = async () => {
        const done = await AsyncStorage.getItem(TUTORIAL_COMPLETED_KEY);
        if (!done) {
          InteractionManager.runAfterInteractions(() => {
            start();
            AsyncStorage.setItem(TUTORIAL_COMPLETED_KEY, 'true');
          });
        }
      };

      tryStartTutorial();
    }, [start]),
  );

  // 테스트용 - 앱 재실행 시 튜토리얼 시작 (추후 제거)
  useEffect(() => {
    AsyncStorage.removeItem('homeTutorialCompleted');
  }, []);

  useEffect(() => {
    if (refresh) {
      console.log('Refresh HomeScreen');
      fetchWeather();
      setRefresh(false);
    }
  }, [refresh]);

  useEffect(() => {
    checkAttendanceOncePerDay();
  }, []);

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchWeather();
    setRefreshing(false);
  };

  /*
  useFocusEffect(
    useCallback(() => {
      fetchWeather();
    }, []),
  );
  */

  useEffect(() => {
    const interval = setInterval(() => {
      console.log('Refreshing weather data...');
      fetchWeather();
    }, 300000);
    return () => clearInterval(interval);
  }, [accessToken]);

  return (
    <View style={globalStyles.container}>
      <StatusBar hidden={true} />

      <WeatherHeader
        accessToken={accessToken}
        weatherData={weatherData}
        onToggleChange={setShowText}
        refreshing={refreshing}
      />

      {weatherData ? (
        <ScrollView
          style={styles.scrollView}
          refreshControl={
            <RefreshControl
              refreshing={refreshing}
              onRefresh={onRefresh}
              tintColor="transparent"
              colors={['#3f7dfd', '#ff5733', '#33ff57']}
            />
          }>
          <Posts
            accessToken={accessToken}
            weatherData={weatherData}
            refreshing={refreshing}
          />
          <HourlyForecast
            accessToken={accessToken}
            weatherData={weatherData}
            showText={showText}
            refreshing={refreshing}
          />
          <AirQuality
            accessToken={accessToken}
            weatherData={weatherData}
            refreshing={refreshing}
          />
          <WeatherGraph
            accessToken={accessToken}
            weatherData={weatherData}
            refreshing={refreshing}
          />
          <KakaoShareButton
            accessToken={accessToken}
            weatherData={weatherData}
          />
        </ScrollView>
      ) : (
        <View style={styles.emptyContainer} />
      )}

      <CopilotStep text="게시글을 작성해 보세요!" order={0} name="write">
        <CopilotView style={styles.floatingButtonWrapper}>
          <TouchableOpacity
            style={[
              styles.floatingButton,
              {backgroundColor: buttonBackgroundColor},
            ]}
            onPress={() => navigation.navigate('PostCreationScreen')}>
            <Image
              source={require('../../assets/images/icon_pencil.png')}
              style={styles.buttonIcon}
            />
          </TouchableOpacity>
        </CopilotView>
      </CopilotStep>
    </View>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    flex: 1,
  },
  emptyContainer: {
    flex: 1,
  },
  floatingButtonWrapper: {
    position: 'absolute',
    bottom: width * 0.05,
    right: width * 0.05,
    width: width * 0.16,
    height: width * 0.16,
    zIndex: 9999, // 👈 Copilot 위치 표시를 방해하지 않도록
  },
  floatingButton: {
    flex: 1, // 👈 부모인 CopilotView에 맞게 채우기
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 999,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.3,
    shadowRadius: 5,
    elevation: 5,
  },
  buttonIcon: {
    width: 25,
    height: 25,
    tintColor: '#FFFFFF',
  },
});

export default HomeScreen;

// 수정 시작
