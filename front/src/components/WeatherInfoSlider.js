import React, {useState, useEffect} from 'react';
import {
  ScrollView,
  View,
  Text,
  StyleSheet,
  Dimensions,
  Image,
  Platform,
} from 'react-native';
import globalStyles from '../globalStyles';
import {fetchWeatherTags, fetchRainForecast} from '../api/api';

const WeatherInfoSlider = ({accessToken}) => {
  const [activeSlide, setActiveSlide] = useState(0);
  const [weatherTags, setWeatherTags] = useState([]);
  const [rainForecast, setRainForecast] = useState(null);

  useEffect(() => {
    const loadWeatherTags = async () => {
      try {
        const fetchedTags = await fetchWeatherTags(accessToken);
        setWeatherTags(fetchedTags);
      } catch (error) {
        console.error('Error loading weather tags:', error);
      }
    };

    const loadRainForecast = async () => {
      try {
        const fetchedRainForecast = await fetchRainForecast(accessToken);
        console.log('Fetched rain forecast:', fetchedRainForecast);
        setRainForecast(fetchedRainForecast);
      } catch (error) {
        console.error('Error loading rain forecast:', error);
      }
    };

    if (accessToken) {
      loadWeatherTags();
      loadRainForecast();
    }
  }, [accessToken]);

  const handleScroll = event => {
    const contentOffsetX = event.nativeEvent.contentOffset.x;
    const slideWidth = Dimensions.get('window').width / 2.2;
    const currentSlide = Math.round(contentOffsetX / slideWidth);
    setActiveSlide(currentSlide);
  };

  return (
    <View style={styles.sliderContainer}>
      <View style={styles.indicatorContainer}>
        {[0, 1].map((_, index) => (
          <View
            key={`indicator-${index}`}
            style={[
              styles.indicator,
              {opacity: index === activeSlide ? 1 : 0.5},
            ]}
          />
        ))}
      </View>
      <ScrollView
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        style={styles.container}
        onScroll={handleScroll}
        scrollEventThrottle={16}>
        <View style={[styles.slide, globalStyles.transparentBackground]}>
          <Text style={styles.title}>우리 동네 날씨</Text>
          <View style={styles.infoContainer}>
            {weatherTags.map((tag, index) => (
              <View key={`tag-${index}`} style={styles.innerBox}>
                <Text style={styles.info}>{tag.text}</Text>
              </View>
            ))}
          </View>
        </View>
        <View style={[styles.slide, globalStyles.transparentBackground]}>
          <Text style={styles.title}>비 올 확률</Text>
          <View style={styles.infoContainer}>
            {rainForecast ? (
              <>
                <Text style={styles.rainInfo}>{rainForecast.rainComment}</Text>
                {rainForecast.willRain && (
                  <Image
                    source={require('../../assets/images/icon_umbrella.png')}
                    style={styles.icon}
                  />
                )}
                <Text style={styles.rainInfo}>{rainForecast.addComment}</Text>
              </>
            ) : (
              <Text style={styles.info}>비 정보를 불러오는 중입니다</Text>
            )}
          </View>
        </View>
      </ScrollView>
    </View>
  );
};

const {width} = Dimensions.get('window');

const styles = StyleSheet.create({
  sliderContainer: {
    alignItems: 'center',
  },
  container: {
    width: width / 2,
    height: 190,
  },
  slide: {
    width: width / 2.05,
    alignItems: 'center',
    paddingVertical: 5,
    paddingHorizontal: 20,
    borderRadius: 10,
    marginHorizontal: 3,
    backgroundColor: 'transparent',
  },
  title: {
    fontSize: 13,
    textAlign: 'center',
    color: '#fff',
    marginTop: 5,
    position: 'absolute',
    top: 5,
  },
  infoContainer: {
    flex: 1,
    justifyContent: 'center',
    width: '100%',
    alignItems: 'center',
    marginTop: 15,
  },
  info: {
    fontSize: 16,
    textAlign: 'center',
    color: '#fff',
    marginVertical: 5,
  },
  rainInfo: {
    fontSize: 14,
    textAlign: 'center',
    color: '#fff',
    marginVertical: Platform.OS === 'ios' ? 0 : -2,
  },
  innerBox: {
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    shadowColor: 'transparent',
    elevation: 0,
    borderRadius: 5,
    marginVertical: 3,
    padding: 3,
    width: '100%',
    justifyContent: 'center',
    alignItems: 'center',
  },
  icon: {
    width: 40,
    height: 40,
    marginVertical: 10,
    alignSelf: 'center',
    tintColor: '#fff',
  },
  indicatorContainer: {
    flexDirection: 'row',
    marginBottom: 7,
  },
  indicator: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#fff',
    marginHorizontal: 3,
  },
});

export default WeatherInfoSlider;
