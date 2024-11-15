import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  StatusBar,
  Dimensions,
  Platform,
} from 'react-native';
import CurrentLocation from '../components/CurrentLocation';
import TemperatureInfo from '../components/TemperatureInfo';
import PostScroll from '../components/PostScroll';
import WeatherShareButton from '../components/WeatherShareButton';
import {fetchWeatherData, fetchMemberInfo} from '../api/api';

const {height} = Dimensions.get('window');

const CommunityScreen = ({accessToken}) => {
  const [refreshPosts, setRefreshPosts] = useState(false);
  const [weatherData, setWeatherData] = useState(null);
  const [backgroundColor, setBackgroundColor] = useState('#2f5af4');
  const [sensitivityText, setSensitivityText] = useState('');

  useEffect(() => {
    if (refreshPosts) {
      setRefreshPosts(false);
    }
  }, [refreshPosts]);

  const handlePostCreated = () => {
    setRefreshPosts(true);
  };

  useEffect(() => {
    const currentHour = new Date().getHours();
    if (currentHour >= 6 && currentHour < 18) {
      setBackgroundColor('#2f5af4');
    } else {
      setBackgroundColor('#1D2837');
    }
  }, []);

  useEffect(() => {
    const getWeatherData = async () => {
      try {
        const data = await fetchWeatherData(accessToken);
        if (data.isSuccess) {
          setWeatherData(data.result);
        } else {
          console.error('Failed to fetch weather data:', data.message);
        }
      } catch (error) {
        console.error('Error fetching weather data:', error.message);
      }
    };

    getWeatherData();
  }, [accessToken]);

  useEffect(() => {
    const getMemberInfo = async () => {
      try {
        const memberInfo = await fetchMemberInfo(accessToken);
        console.log('Fetched member info:', memberInfo);

        if (memberInfo && memberInfo.sensitivity) {
          switch (memberInfo.sensitivity) {
            case 'HOT':
              setSensitivityText('더위를 많이 타는');
              break;
            case 'NONE':
              setSensitivityText('평범한');
              break;
            case 'COLD':
              setSensitivityText('추위를 많이 타는');
              break;
            default:
              setSensitivityText('알 수 없는');
              break;
          }
        }
      } catch (error) {
        console.error('Error fetching member info:', error.message);
      }
    };

    getMemberInfo();
  }, [accessToken]);

  return (
    <View style={[styles.container, {backgroundColor: backgroundColor}]}>
      <StatusBar hidden={true} />
      <View style={styles.topSpacer} />
      <View style={styles.topContainer}>
        <WeatherShareButton
          onPostCreated={handlePostCreated}
          accessToken={accessToken}
        />
        <View style={styles.rightContainer}>
          <CurrentLocation accessToken={accessToken} />
          <TemperatureInfo accessToken={accessToken} />
        </View>
      </View>
      <Text style={styles.text}>
        ‘{sensitivityText}’ 유형이 가장 많이 공감했어요
      </Text>
      <PostScroll
        accessToken={accessToken}
        refreshPosts={refreshPosts}
        onRefreshComplete={() => setRefreshPosts(false)}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  topSpacer: {
    height: Platform.OS === 'ios' ? height * 0.05 : height * 0.03,
  },
  topContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '95%',
    paddingBottom: Platform.OS === 'ios' ? 15 : 10,
  },
  rightContainer: {
    width: '50%',
    justifyContent: 'space-between',
    paddingBottom: 10,
  },
  text: {
    color: '#fff',
    textAlign: 'center',
    paddingBottom: Platform.OS === 'ios' ? 10 : 15,
  },
});

export default CommunityScreen;
