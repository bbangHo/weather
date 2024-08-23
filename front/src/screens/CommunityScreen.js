import React from 'react';
import {View, Text, StyleSheet, StatusBar} from 'react-native';
import CurrentLocation from '../components/CurrentLocation';
import TemperatureInfo from '../components/TemperatureInfo';
import PostScroll from '../components/PostScroll';
import WeatherShareButton from '../components/WeatherShareButton';

const CommunityScreen = () => {
  return (
    <View style={styles.container}>
      <StatusBar hidden={true} />
      <View style={styles.topSpacer} />
      <View style={styles.topContainer}>
        <WeatherShareButton />
        <View style={styles.rightContainer}>
          <CurrentLocation />
          <TemperatureInfo />
        </View>
      </View>
      <Text style={styles.text}>
        ‘추위를 많이타는’ 유형이 가장 많이 공감했어요
      </Text>
      <PostScroll />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#2f5af4',
  },
  topSpacer: {
    height: 50,
  },
  topContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '95%',
    paddingBottom: 15,
  },
  rightContainer: {
    width: '50%',
    justifyContent: 'space-between',
    paddingBottom: 10,
  },
  text: {
    color: '#fff',
    textAlign: 'center',
    paddingBottom: 10,
  },
});

export default CommunityScreen;
