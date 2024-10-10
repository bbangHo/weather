import React, {useState, useEffect} from 'react';
import {
  ScrollView,
  View,
  Text,
  StyleSheet,
  Dimensions,
  Image,
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import globalStyles from '../globalStyles';
import {fetchWeatherTags, fetchRainForecast} from '../api/api';

const WeatherInfoSlider = ({accessToken, memberId}) => {
  const [activeSlide, setActiveSlide] = useState(0);
  const [weatherTags, setWeatherTags] = useState([]);
  const [rainForecast, setRainForecast] = useState(null);

  useEffect(() => {
    const loadWeatherTags = async () => {
      try {
        const fetchedTags = await fetchWeatherTags(accessToken, memberId);
        setWeatherTags(fetchedTags);
      } catch (error) {
        console.error('Error loading weather tags:', error);
      }
    };

    const loadRainForecast = async () => {
      try {
        const fetchedRainForecast = await fetchRainForecast(
          accessToken,
          memberId,
        );
        console.log('Fetched rain forecast:', fetchedRainForecast);
        setRainForecast(fetchedRainForecast);
      } catch (error) {
        console.error('Error loading rain forecast:', error);
      }
    };

    if (accessToken && memberId) {
      loadWeatherTags();
      loadRainForecast();
    }
  }, [accessToken, memberId]);

  const handleScroll = event => {
    const contentOffsetX = event.nativeEvent.contentOffset.x;
    const slideWidth = Dimensions.get('window').width / 2.2;
    const currentSlide = Math.round(contentOffsetX / slideWidth);
    setActiveSlide(currentSlide);
  };

  return (
    <View style={styles.sliderContainer}>
      <View style={styles.indicatorContainer}>
        {[0, 1, 2].map((_, index) => (
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
                <Image
                  source={
                    rainForecast.willRain
                      ? require('../../assets/images/icon_umbrella.png')
                      : require('../../assets/images/icon_clear.png')
                  }
                  style={styles.icon}
                />
                <Text style={styles.info}>{rainForecast.comment}</Text>
              </>
            ) : (
              <Text style={styles.info}>비 정보를 불러오는 중...</Text>
            )}
          </View>
        </View>
        <View style={[styles.slide, globalStyles.transparentBackground]}>
          <Text style={styles.title}>이번 주 날씨</Text>
          <View style={styles.infoContainer}>
            <View style={styles.weeklyForecast}>
              <View style={styles.dayForecast}>
                <Text style={styles.day}>월</Text>
                <Icon name="rainy" style={styles.weatherIcon} />
              </View>
              <View style={styles.dayForecast}>
                <Text style={styles.day}>화</Text>
                <Icon name="sunny" style={styles.weatherIcon} />
              </View>
              <View style={styles.dayForecast}>
                <Text style={styles.day}>수</Text>
                <Icon name="partly-sunny" style={styles.weatherIcon} />
              </View>
              <View style={styles.dayForecast}>
                <Text style={styles.day}>목</Text>
                <Icon name="rainy" style={styles.weatherIcon} />
              </View>
            </View>
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
    height: 180,
  },
  slide: {
    width: width / 2.1,
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
    width: 50,
    height: 50,
    marginVertical: 10,
    alignSelf: 'center',
    tintColor: '#fff',
  },
  weeklyForecast: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    width: '100%',
  },
  dayForecast: {
    alignItems: 'center',
  },
  day: {
    fontSize: 16,
    marginBottom: 8,
    color: '#fff',
  },
  weatherIcon: {
    fontSize: 25,
    color: '#fff',
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
