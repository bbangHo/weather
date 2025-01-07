import React, {useState, useCallback, useEffect} from 'react';
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
import WeatherHeader from '../components/WeatherHeader';
import HourlyForecast from '../components/HourlyForecast';
import AirQuality from '../components/AirQuality';
import WeatherGraph from '../components/WeatherGraph';
import Posts from '../components/Posts';
import ToggleViewButton from '../components/ToggleViewButton';
import globalStyles from '../globalStyles';
import {fetchWeatherData} from '../api/api';

const {width, height} = Dimensions.get('window');

const HomeScreen = ({accessToken, navigation}) => {
  const [showText, setShowText] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [buttonBackgroundColor, setButtonBackgroundColor] = useState('#3f7dfd');

  const isNightTime = () => {
    const currentHour = new Date().getHours();
    return currentHour >= 18 || currentHour < 6;
  };

  const updateButtonBackgroundColor = async () => {
    try {
      const weatherData = await fetchWeatherData(accessToken);
      const isCloudyOrRainy = weatherData?.result?.weatherPerHourList.some(
        item => {
          const hour = new Date(item.hour).getHours();
          const currentHour = new Date().getHours();
          return (
            hour === currentHour && (item.skyType === 'CLOUDY' || item.rain > 0)
          );
        },
      );

      if (isNightTime()) {
        setButtonBackgroundColor('#1D2837');
      } else if (isCloudyOrRainy) {
        setButtonBackgroundColor('#7998a6');
      } else {
        setButtonBackgroundColor('#3f7dfd');
      }
    } catch (error) {
      console.error('Error updating button background color:', error);
    }
  };

  const loadData = async (showRefreshIndicator = false) => {
    if (showRefreshIndicator) {
      setRefreshing(true);
    }
    await updateButtonBackgroundColor();
    await new Promise(resolve => setTimeout(resolve, 1000));
    setRefreshing(false);
  };

  useFocusEffect(
    useCallback(() => {
      loadData(false);
    }, []),
  );

  useEffect(() => {
    const interval = setInterval(() => {
      console.log('Updating button background color...');
      updateButtonBackgroundColor();
    }, 60000);
    return () => clearInterval(interval);
  }, [accessToken]);

  return (
    <View style={globalStyles.container}>
      <StatusBar hidden={true} />
      <WeatherHeader accessToken={accessToken} onToggleChange={setShowText} />

      <ScrollView
        style={globalStyles.container}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={() => loadData(true)}
          />
        }>
        <Posts accessToken={accessToken} refreshing={refreshing} />
        <ToggleViewButton showText={showText} setShowText={setShowText} />
        <HourlyForecast
          accessToken={accessToken}
          showText={showText}
          refreshing={refreshing}
        />
        <AirQuality accessToken={accessToken} refreshing={refreshing} />
        <WeatherGraph accessToken={accessToken} refreshing={refreshing} />
      </ScrollView>

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
    </View>
  );
};

const styles = StyleSheet.create({
  floatingButton: {
    position: 'absolute',
    bottom: width * 0.05,
    right: width * 0.05,
    width: width * 0.16,
    height: width * 0.16,
    borderRadius: 999,
    justifyContent: 'center',
    alignItems: 'center',
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
