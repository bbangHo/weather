import React, {useState, useEffect, useRef} from 'react';
import {
  View,
  StyleSheet,
  StatusBar,
  TouchableOpacity,
  Image,
  Animated,
  VirtualizedList,
  Dimensions,
} from 'react-native';
import InterestItem from '../components/InterestItem';
import InterestPostScroll from '../components/InterestPostScroll';
import {fetchWeatherData} from '../api/api';
import {useNavigation} from '@react-navigation/native';

const arrowDownIcon = require('../../assets/images/icon_arrowDown.png');
const shareIcon = require('../../assets/images/icon_share2.png');

const InterestScreen = ({accessToken}) => {
  const [selectedHobby, setSelectedHobby] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [weatherData, setWeatherData] = useState(null);
  const [showPostScroll, setShowPostScroll] = useState(false);
  const [currentIcon, setCurrentIcon] = useState(arrowDownIcon);
  const [backgroundColor, setBackgroundColor] = useState('#2f5af4');
  const scrollViewRef = useRef(null);
  const translateY = useRef(new Animated.Value(0)).current;

  const navigation = useNavigation();

  useEffect(() => {
    const currentHour = new Date().getHours();
    setBackgroundColor(
      currentHour >= 6 && currentHour < 18 ? '#2f5af4' : '#1D2837',
    );
  }, []);

  useEffect(() => {
    const getWeatherData = async () => {
      try {
        const data = await fetchWeatherData(accessToken);
        if (data.isSuccess) setWeatherData(data.result);
        else console.error('Failed to fetch weather data:', data.message);
      } catch (error) {
        console.error('Error fetching weather data:', error.message);
      }
    };
    getWeatherData();
  }, [accessToken]);

  const handleScroll = event => {
    const {layoutMeasurement, contentOffset} = event.nativeEvent;
    const scrollThreshold = layoutMeasurement.height / 2;

    if (contentOffset.y >= scrollThreshold && !showPostScroll) {
      setShowPostScroll(true);
      setCurrentIcon(shareIcon);
      Animated.timing(translateY, {
        toValue: -layoutMeasurement.height,
        duration: 300,
        useNativeDriver: true,
      }).start();
    } else if (contentOffset.y < scrollThreshold && showPostScroll) {
      setShowPostScroll(false);
      setCurrentIcon(arrowDownIcon);
      Animated.timing(translateY, {
        toValue: 0,
        duration: 300,
        useNativeDriver: true,
      }).start();
    }
  };

  const handleIconPress = () => {
    if (showPostScroll) {
      navigation.navigate('InterestPostCreationScreen');
    } else {
      scrollViewRef.current.scrollToEnd({animated: true});
    }
  };

  const getItemCount = () => (showPostScroll ? 2 : 1);
  const getItem = (data, index) => data[index];

  const renderItem = ({item}) => {
    if (item === 'InterestItem') {
      return (
        <Animated.View
          style={[styles.animatedContainer, {transform: [{translateY}]}]}>
          <InterestItem
            accessToken={accessToken}
            selectedHobby={selectedHobby}
            setSelectedHobby={setSelectedHobby}
            modalVisible={modalVisible}
            setModalVisible={setModalVisible}
          />
        </Animated.View>
      );
    } else if (item === 'InterestPostScroll' && selectedHobby) {
      return (
        <InterestPostScroll
          accessToken={accessToken}
          selectedHobby={selectedHobby}
        />
      );
    }
  };

  return (
    <View style={[styles.container, {backgroundColor: backgroundColor}]}>
      <StatusBar hidden={true} />

      <View style={styles.fixedArrowContainer}>
        <TouchableOpacity onPress={handleIconPress}>
          <Image
            source={currentIcon}
            style={
              currentIcon === arrowDownIcon
                ? styles.arrowIcon
                : styles.shareIcon
            }
          />
        </TouchableOpacity>
      </View>

      <VirtualizedList
        ref={scrollViewRef}
        onScroll={handleScroll}
        scrollEventThrottle={16}
        data={['InterestItem', 'InterestPostScroll']}
        getItemCount={getItemCount}
        getItem={getItem}
        renderItem={renderItem}
        keyExtractor={item => item}
        contentContainerStyle={styles.scrollViewContent}
      />
    </View>
  );
};

const {height} = Dimensions.get('window');

const styles = StyleSheet.create({
  container: {
    flex: 1,
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
    marginBottom: Platform.OS === 'ios' ? 4 : 10,
  },
  shareIcon: {
    width: 30,
    height: 30,
    resizeMode: 'contain',
    tintColor: '#c4c4c4',
    marginBottom: Platform.OS === 'ios' ? 4 : 10,
  },
  scrollViewContent: {
    marginTop: Platform.OS === 'ios' ? 10 : 40,
    minHeight: height * 1.5,
    paddingBottom: 20,
  },
  animatedContainer: {
    flex: 1,
  },
});

export default InterestScreen;
