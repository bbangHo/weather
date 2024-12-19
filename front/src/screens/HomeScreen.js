import React, {useState, useCallback} from 'react';
import {ScrollView, View, StatusBar, RefreshControl} from 'react-native';
import {useFocusEffect} from '@react-navigation/native';
import WeatherHeader from '../components/WeatherHeader';
import HourlyForecast from '../components/HourlyForecast';
import AirQuality from '../components/AirQuality';
import WeatherGraph from '../components/WeatherGraph';
import Posts from '../components/Posts';
import ToggleViewButton from '../components/ToggleViewButton';
import FloatingActionButton from '../components/FloatingActionButton';
import globalStyles from '../globalStyles';

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

      <FloatingActionButton
        onPress={() => navigation.navigate('PostCreationScreen')}
      />
    </View>
  );
};

export default HomeScreen;
