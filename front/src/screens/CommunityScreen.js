import React, {useState, useCallback, useEffect} from 'react';
import {
  View,
  StatusBar,
  StyleSheet,
  ScrollView,
  RefreshControl,
  TouchableOpacity,
  Image,
  Dimensions,
} from 'react-native';
import WeatherHeaderCommunity from '../components/WeatherHeaderCommunity';
import PostScroll from '../components/PostScroll';
import {useFocusEffect} from '@react-navigation/native';
import {fetchWeatherData} from '../api/api';

const {width, height} = Dimensions.get('window');

const CommunityScreen = ({accessToken, navigation}) => {
  const [refreshPosts, setRefreshPosts] = useState(false);
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

  const loadPosts = async (showRefreshIndicator = false) => {
    if (showRefreshIndicator) {
      setRefreshing(true);
    }
    await updateButtonBackgroundColor();
    setRefreshPosts(true);
    await new Promise(resolve => setTimeout(resolve, 1000));
    setRefreshPosts(false);
    setRefreshing(false);
  };

  useFocusEffect(
    useCallback(() => {
      loadPosts(false);
    }, []),
  );

  useEffect(() => {
    const interval = setInterval(() => {
      console.log('Updating button background color in CommunityScreen...');
      updateButtonBackgroundColor();
    }, 60000);
    return () => clearInterval(interval);
  }, [accessToken]);

  return (
    <View style={styles.container}>
      <StatusBar hidden={true} />
      <WeatherHeaderCommunity accessToken={accessToken} />

      <ScrollView
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={() => loadPosts(true)}
          />
        }>
        <PostScroll
          accessToken={accessToken}
          refreshPosts={refreshPosts}
          onRefreshComplete={() => setRefreshPosts(false)}
        />
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
  container: {
    flex: 1,
    backgroundColor: '#F5F6FA',
  },
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

export default CommunityScreen;
