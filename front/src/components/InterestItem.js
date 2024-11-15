import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Modal,
  FlatList,
  Image,
  Dimensions,
  ScrollView,
  Platform,
} from 'react-native';
import {
  fetchUserLocation,
  fetchLocationInfo,
  submitAddress,
  fetchWeatherData,
  fetchExtraWeatherInfo,
} from '../api/api';

const InterestItem = ({
  accessToken,
  selectedHobby,
  setSelectedHobby,
  modalVisible,
  setModalVisible,
  setLocationId,
}) => {
  const [userLocation, setUserLocation] = useState(null);
  const [addressModalVisible, setAddressModalVisible] = useState(false);
  const [searchResults, setSearchResults] = useState([]);
  const [province, setProvince] = useState('');
  const [city, setCity] = useState('');
  const [street, setStreet] = useState('');
  const [step, setStep] = useState(0);

  const [hobbiesWeatherData, setHobbiesWeatherData] = useState([]);
  const [pm10Grade, setPm10Grade] = useState(null);
  const [locationId, setLocalLocationId] = useState(null);

  useEffect(() => {
    const loadUserLocation = async () => {
      try {
        const locationData = await fetchUserLocation(accessToken);
        setUserLocation(locationData);
        if (locationData && locationData.id) {
          setLocationId(locationData.id);
          setLocalLocationId(locationData.id);
          console.log('Fetched locationId:', locationData.id);
        }
      } catch (error) {
        console.error('위치 정보를 불러오는 중 에러가 발생했습니다:', error);
      }
    };

    if (accessToken) {
      loadUserLocation();
    }
  }, [accessToken]);

  useEffect(() => {
    const loadWeatherData = async () => {
      try {
        const weatherData = await fetchWeatherData(accessToken, locationId);
        console.log('Fetched weather data:', weatherData);

        if (weatherData.isSuccess && weatherData.result.weatherPerHourList) {
          const formattedData = weatherData.result.weatherPerHourList
            .slice(0, 10)
            .map(item => ({
              hour: new Date(item.hour).getHours() + '시',
              skyType: item.skyType,
              tmp: item.tmp,
              rain: item.rain,
            }));

          setHobbiesWeatherData(formattedData);
        } else {
          console.error('Weather data fetch failed:', weatherData.message);
        }
      } catch (error) {
        console.error('Error fetching weather data:', error);
      }
    };

    const loadExtraWeatherInfo = async () => {
      try {
        const extraWeatherData = await fetchExtraWeatherInfo(
          accessToken,
          locationId,
        );
        console.log('Fetched extra weather info:', extraWeatherData);

        if (extraWeatherData?.pm10Grade !== undefined) {
          setPm10Grade(extraWeatherData.pm10Grade);
        } else {
          console.error(
            'pm10Grade is missing in extra weather data:',
            extraWeatherData,
          );
          setPm10Grade(0);
        }
      } catch (error) {
        console.error(
          'Error fetching extra weather info:',
          error.message || error,
        );
        setPm10Grade(0);
      }
    };

    loadWeatherData();
    loadExtraWeatherInfo();
  }, [userLocation, accessToken, locationId]);

  const fetchLocationData = async currentStep => {
    try {
      let results;
      if (currentStep === 1) {
        results = await fetchLocationInfo(accessToken, '', '');
      } else if (currentStep === 2) {
        results = await fetchLocationInfo(accessToken, province, '');
      } else if (currentStep === 3) {
        results = await fetchLocationInfo(accessToken, province, city);
      }

      setSearchResults(results?.result || []);
    } catch (error) {
      console.error('주소 검색 중 에러:', error);
    }
  };

  const handleDropdownPress = newStep => {
    setStep(newStep);
    fetchLocationData(newStep);
  };

  const handleAddressSelect = selected => {
    if (step === 1) {
      setProvince(selected);
      setCity('');
      setStreet('');
      setStep(2);
    } else if (step === 2) {
      setCity(selected);
      setStreet('');
      setStep(3);
    } else if (step === 3) {
      setStreet(selected);
      setUserLocation({province, city, street: selected});
    }
    setSearchResults([]);
  };

  const handleLocationPress = () => {
    setAddressModalVisible(true);
    setStep(1);
    setProvince('');
    setCity('');
    setStreet('');
  };

  const handleSubmitAddress = async () => {
    if (!province || !city || !street) {
      setAddressModalVisible(false);
      return;
    }

    try {
      const response = await submitAddress(accessToken, province, city, street);
      console.log('주소 생성:', response);

      if (response.result && response.result.id) {
        setUserLocation(response.result);
        setLocationId(response.result.id);
        setLocalLocationId(response.result.id);
        console.log(
          'Fetched locationId (id) from address:',
          response.result.id,
        );
      }

      setAddressModalVisible(false);
    } catch (error) {
      console.error('주소 생성 중 에러 발생:', error);
    }
  };

  const handleHobbyPress = hobby => {
    setSelectedHobby(hobby);
    setModalVisible(false);
  };

  const renderDropdown = (placeholder, value, onPress, visible) => (
    <View style={styles.dropdownContainer}>
      <TouchableOpacity onPress={onPress} style={styles.dropdown}>
        <Text
          style={[styles.dropdownText, value && styles.selectedDropdownText]}>
          {value || placeholder}
        </Text>
      </TouchableOpacity>
      {visible && (
        <FlatList
          data={searchResults}
          renderItem={({item}) => (
            <TouchableOpacity onPress={() => handleAddressSelect(item)}>
              <Text style={styles.addressItem}>{item}</Text>
            </TouchableOpacity>
          )}
          keyExtractor={(item, index) => index.toString()}
          style={styles.dropdownFlatList}
        />
      )}
    </View>
  );

  const hobbies = [
    {
      id: 1,
      name: '런닝',
      postType: 'RUN',
      icon: require('../../assets/images/icon_interest_run.png'),
    },
    {
      id: 2,
      name: '등산',
      postType: 'HIKING',
      icon: require('../../assets/images/icon_interest_hiking.png'),
    },
    {
      id: 3,
      name: '반려동물 산책',
      postType: 'PET',
      icon: require('../../assets/images/icon_interest_pet.png'),
    },
  ];

  const getHobbyIcon = () => {
    switch (selectedHobby?.postType) {
      case 'HIKING':
        return require('../../assets/images/icon_interest_hiking.png');
      case 'PET':
        return require('../../assets/images/icon_interest_pet.png');
      case 'RUN':
      default:
        return require('../../assets/images/icon_interest_run.png');
    }
  };

  const getGrade = (rain, tmp) => {
    if (rain > 0) return '나쁨';
    if (pm10Grade === 4) return '매우 나쁨';
    if (pm10Grade === 3) return '나쁨';
    if (tmp >= 33 || tmp <= -12) return '매우 나쁨';
    if (tmp >= 28 || tmp <= 4) return '나쁨';
    if ((tmp >= 24 && tmp <= 27) || (tmp >= 5 && tmp <= 14)) return '보통';
    if (tmp >= 15 && tmp <= 23) return '좋음';
    return '정보 없음';
  };

  const getGradeColor = grade => {
    switch (grade) {
      case '좋음':
        return '#81BEF7';
      case '보통':
        return '#A9F5A9';
      case '나쁨':
        return '#F78181';
      case '매우 나쁨':
        return '#DB4455';
      default:
        return '#c4c4c4';
    }
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={handleLocationPress}>
        <Text style={styles.locationText}>
          {userLocation
            ? `${userLocation.province} ${userLocation.city} ${userLocation.street}`
            : '위치 정보를 불러오는 중...'}
        </Text>
      </TouchableOpacity>
      <View style={styles.weatherIconContainer}>
        {hobbiesWeatherData.length > 0 && (
          <>
            <Image
              source={getHobbyIcon()}
              style={[
                styles.weatherIcon,
                {
                  tintColor: getGradeColor(
                    getGrade(
                      hobbiesWeatherData[0].rain,
                      hobbiesWeatherData[0].tmp,
                    ),
                  ),
                },
              ]}
            />
            <Text
              style={[
                styles.weatherText,
                {
                  color: getGradeColor(
                    getGrade(
                      hobbiesWeatherData[0].rain,
                      hobbiesWeatherData[0].tmp,
                    ),
                  ),
                },
              ]}>
              {getGrade(hobbiesWeatherData[0].rain, hobbiesWeatherData[0].tmp)}
            </Text>
          </>
        )}
      </View>

      <View style={styles.hobbyContainer}>
        <TouchableOpacity onPress={() => setModalVisible(true)}>
          <View style={styles.hobbyTextContainer}>
            <Text style={styles.hobbyText}>
              {selectedHobby ? selectedHobby.name : '취미 선택 ▼'}
            </Text>
          </View>
        </TouchableOpacity>
      </View>
      <Modal
        visible={modalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setModalVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalHobbyContainer}>
            <Text style={styles.modalTitle}>취미 선택</Text>
            <FlatList
              data={hobbies}
              numColumns={2}
              renderItem={({item}) => (
                <TouchableOpacity
                  style={[
                    styles.hobbyButton,
                    selectedHobby?.id === item.id && styles.selectedHobby,
                  ]}
                  onPress={() => handleHobbyPress(item)}>
                  <Image
                    source={item.icon}
                    style={[
                      styles.hobbyIcon,
                      {
                        tintColor:
                          selectedHobby?.id === item.id ? '#fff' : '#3f51b5',
                      },
                    ]}
                  />
                  <Text
                    style={[
                      styles.hobbyButtonText,
                      selectedHobby?.id === item.id && styles.selectedHobbyText,
                    ]}>
                    {item.name}
                  </Text>
                </TouchableOpacity>
              )}
              keyExtractor={item => item.id.toString()}
              contentContainerStyle={styles.hobbyList}
            />
            <TouchableOpacity
              style={styles.applyButton}
              onPress={() => setModalVisible(false)}>
              <Text style={styles.applyText}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
      <Modal
        visible={addressModalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setAddressModalVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContainer}>
            <Text style={styles.modalTitle}>주소 검색</Text>

            {renderDropdown(
              '도(광역시)',
              province,
              () => handleDropdownPress(1),
              step === 1,
            )}
            {renderDropdown(
              '시군구',
              city,
              () => handleDropdownPress(2),
              step === 2 && province,
            )}
            {renderDropdown(
              '읍면동',
              street,
              () => handleDropdownPress(3),
              step === 3 && city,
            )}

            <TouchableOpacity
              style={styles.applyButton}
              onPress={handleSubmitAddress}>
              <Text style={styles.applyText}>확인</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
      <ScrollView style={styles.hourlyContainer} horizontal>
        {hobbiesWeatherData.map((item, index) => {
          const grade = getGrade(item.rain, item.tmp);
          const gradeColor = getGradeColor(grade);

          return (
            <View key={index} style={styles.timeSlot}>
              <Text style={styles.textTime}>{item.hour}</Text>
              <Image
                source={getHobbyIcon()}
                style={[styles.icon, {tintColor: gradeColor}]}
              />
              <Text style={[styles.gradeText, {color: gradeColor}]}>
                {grade}
              </Text>
              <Text style={styles.tmpText}>{`${item.tmp}°C`}</Text>
              <Text style={styles.rainText}>{`${item.rain}mm`}</Text>
            </View>
          );
        })}
      </ScrollView>
    </View>
  );
};

const {width} = Dimensions.get('window');
const styles = StyleSheet.create({
  dropdownContainer: {
    width: '100%',
    paddingVertical: 10,
  },
  dropdown: {
    padding: 10,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 5,
    backgroundColor: '#fff',
  },
  dropdownText: {
    fontSize: 16,
    color: '#888',
  },
  selectedDropdownText: {
    color: '#333',
  },
  dropdownFlatList: {
    maxHeight: 300,
  },
  container: {
    alignItems: 'center',
    paddingTop: Platform.OS === 'ios' ? 10 : 0,
    marginTop: 70,
  },
  locationText: {
    fontSize: 20,
    color: '#fff',
    marginBottom: 10,
  },
  weatherIconContainer: {
    alignItems: 'center',
    marginVertical: 30,
  },
  weatherIcon: {
    width: 60,
    height: 60,
  },
  weatherText: {
    color: '#fff',
    fontSize: 18,
    marginTop: 10,
  },
  hobbyContainer: {
    alignItems: 'center',
    marginVertical: 20,
  },
  hobbyTextContainer: {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 10,
    width: width * 0.35,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 10,
    paddingBottom: Platform.OS === 'ios' ? 10 : 15,
  },
  hobbyText: {
    fontSize: 20,
    color: '#fff',
    textAlign: 'center',
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContainer: {
    backgroundColor: '#fff',
    width: width * 0.9,
    borderRadius: 10,
    padding: 20,
    alignItems: 'center',
  },
  modalHobbyContainer: {
    backgroundColor: '#fff',
    width: width * 1,
    borderRadius: 10,
    padding: 20,
    alignItems: 'center',
  },
  modalTitle: {
    fontSize: 20,
    marginBottom: 20,
    color: '#3f51b5',
  },
  hobbyButton: {
    paddingVertical: 20,
    margin: 10,
    borderWidth: 1,
    borderColor: '#3f51b5',
    borderRadius: 10,
    alignItems: 'center',
    justifyContent: 'center',
    width: width * 0.4,
  },
  hobbyButtonText: {
    fontSize: 14,
    color: '#3f51b5',
    marginTop: 5,
    textAlign: 'center',
  },
  selectedHobby: {
    backgroundColor: '#3f51b5',
  },
  selectedHobbyText: {
    color: '#fff',
  },
  hobbyIcon: {
    width: 30,
    height: 30,
  },
  applyButton: {
    backgroundColor: '#3f51b5',
    paddingVertical: 10,
    paddingHorizontal: 40,
    borderRadius: 5,
    marginTop: 20,
  },
  applyText: {
    color: '#fff',
    fontSize: 16,
  },
  searchInputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '100%',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 10,
    marginVertical: 10,
    flex: 1,
  },
  searchButton: {
    backgroundColor: '#3f51b5',
    padding: 10,
    marginLeft: 10,
    borderRadius: 5,
  },
  searchButtonText: {
    color: '#fff',
  },
  addressItem: {
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
  },
  closeButton: {
    backgroundColor: '#3f51b5',
    paddingVertical: 10,
    paddingHorizontal: 40,
    borderRadius: 5,
    marginTop: 20,
    alignItems: 'center',
  },
  hourlyContainer: {
    flexDirection: 'row',
    marginVertical: 10,
    paddingHorizontal: 5,
    marginTop: 70,
  },
  timeSlot: {
    alignItems: 'center',
    marginHorizontal: 5,
    width: 80,
    padding: 10,
    paddingVertical: 25,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 10,
  },
  textTime: {
    color: '#fff',
    fontSize: 13,
    marginBottom: 15,
    textAlign: 'center',
  },
  icon: {
    width: 45,
    height: 45,
    marginBottom: 2,
  },
  tmpText: {
    color: '#fff',
    fontSize: 14,
    marginTop: 20,
    textAlign: 'center',
  },
  rainText: {
    color: 'skyblue',
    fontSize: 12,
    marginTop: 3,
    textAlign: 'center',
  },
  gradeText: {
    fontSize: 12,
    marginTop: 5,
    textAlign: 'center',
  },
});

export default InterestItem;
