import React, {useState, useCallback} from 'react';
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

const {width, height} = Dimensions.get('window');

const HomeScreen = ({accessToken, navigation}) => {
  const [showText, setShowText] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const loadData = async (showRefreshIndicator = false) => {
    if (showRefreshIndicator) {
      setRefreshing(true);
    }
    await new Promise(resolve => setTimeout(resolve, 1000));
    setRefreshing(false);
  };

  useFocusEffect(
    useCallback(() => {
      loadData(false);
    }, []),
  );

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
        <Posts accessToken={accessToken} />
        <ToggleViewButton showText={showText} setShowText={setShowText} />
        <HourlyForecast accessToken={accessToken} showText={showText} />
        <AirQuality accessToken={accessToken} />
        <WeatherGraph accessToken={accessToken} />
      </ScrollView>

      <TouchableOpacity
        style={styles.floatingButton}
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
    backgroundColor: '#3f7dfd',
    borderRadius: 30,
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
