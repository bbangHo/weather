import React, {useState, useEffect, useRef} from 'react';
import {
  View,
  ScrollView,
  StyleSheet,
  StatusBar,
  TouchableOpacity,
  Image,
  Animated,
  Dimensions,
} from 'react-native';
import InterestItem from '../components/InterestItem';
import PostScroll from '../components/PostScroll';
import {fetchWeatherData} from '../api/api';

const arrowDownIcon = require('../../assets/images/icon_arrowDown.png');
const shareIcon = require('../../assets/images/icon_share2.png');

const InterestScreen = ({accessToken, memberId}) => {
  const [selectedHobby, setSelectedHobby] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [weatherData, setWeatherData] = useState(null);
  const [showPostScroll, setShowPostScroll] = useState(false);
  const [currentIcon, setCurrentIcon] = useState(arrowDownIcon);
  const scrollViewRef = useRef(null);
  const translateY = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    const getWeatherData = async () => {
      try {
        const data = await fetchWeatherData(memberId, accessToken);
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
  }, [accessToken, memberId]);

  const handleScroll = event => {
    const {layoutMeasurement, contentOffset, contentSize} = event.nativeEvent;
    const scrollThreshold = layoutMeasurement.height / 2;

    if (contentOffset.y >= scrollThreshold) {
      if (!showPostScroll) {
        setShowPostScroll(true);
        setCurrentIcon(shareIcon);
        Animated.timing(translateY, {
          toValue: -layoutMeasurement.height,
          duration: 300,
          useNativeDriver: true,
        }).start();
      }
    } else {
      if (showPostScroll) {
        setShowPostScroll(false);
        setCurrentIcon(arrowDownIcon);
        Animated.timing(translateY, {
          toValue: 0,
          duration: 300,
          useNativeDriver: true,
        }).start();
      }
    }
  };

  const handleIconPress = () => {
    if (showPostScroll) {
      console.log('Share button pressed');
    } else {
      scrollViewRef.current.scrollToEnd({animated: true});
    }
  };

  const getIconStyle = () => {
    return currentIcon === arrowDownIcon ? styles.arrowIcon : styles.shareIcon;
  };

  return (
    <View style={styles.container}>
      <StatusBar hidden={true} />

      <View style={styles.fixedArrowContainer}>
        <TouchableOpacity onPress={handleIconPress}>
          <Image source={currentIcon} style={getIconStyle()} />
        </TouchableOpacity>
      </View>

      <ScrollView
        ref={scrollViewRef}
        onScroll={handleScroll}
        scrollEventThrottle={16}
        contentContainerStyle={styles.scrollViewContent}>
        <Animated.View
          style={[styles.animatedContainer, {transform: [{translateY}]}]}>
          <InterestItem
            weatherData={weatherData}
            selectedHobby={selectedHobby}
            setSelectedHobby={setSelectedHobby}
            modalVisible={modalVisible}
            setModalVisible={setModalVisible}
          />
        </Animated.View>

        {showPostScroll && (
          <View style={styles.postScrollContainer}>
            <PostScroll accessToken={accessToken} memberId={memberId} />
          </View>
        )}
      </ScrollView>
    </View>
  );
};

const {height} = Dimensions.get('window');

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#2f5af4',
  },
  fixedArrowContainer: {
    position: 'absolute',
    bottom: 20,
    left: '50%',
    transform: [{translateX: -15}],
    zIndex: 1,
    alignItems: 'center',
  },
  arrowIcon: {
    width: 30,
    height: 30,
    resizeMode: 'contain',
    tintColor: '#fff',
  },
  shareIcon: {
    width: 30,
    height: 30,
    resizeMode: 'contain',
    tintColor: '#c4c4c4',
    backgroundColor: '#fff',
    borderRadius: 50,
  },
  scrollViewContent: {
    minHeight: height * 1.5,
  },
  animatedContainer: {
    flex: 1,
    backgroundColor: '#2f5af4',
  },
  postScrollContainer: {
    flex: 1,
    paddingTop: 0,
    backgroundColor: '#2f5af4',
  },
});

export default InterestScreen;
