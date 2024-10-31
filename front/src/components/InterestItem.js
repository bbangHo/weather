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
} from 'react-native';
import {fetchUserLocation, fetchLocationInfo, submitAddress} from '../api/api';

const InterestItem = ({
  accessToken,
  selectedHobby,
  setSelectedHobby,
  modalVisible,
  setModalVisible,
}) => {
  const [userLocation, setUserLocation] = useState(null);
  const [addressModalVisible, setAddressModalVisible] = useState(false);
  const [searchResults, setSearchResults] = useState([]);
  const [province, setProvince] = useState('');
  const [city, setCity] = useState('');
  const [street, setStreet] = useState('');
  const [step, setStep] = useState(0);

  useEffect(() => {
    const loadUserLocation = async () => {
      try {
        const locationData = await fetchUserLocation(accessToken);
        setUserLocation(locationData);
      } catch (error) {
        console.error('위치 정보를 불러오는 중 에러가 발생했습니다:', error);
      }
    };

    if (accessToken) {
      loadUserLocation();
    }
  }, [accessToken]);

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
      setUserLocation(response.result);
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

  const getWeatherIcon = skyType => {
    switch (skyType) {
      case 'CLEAR':
        return require('../../assets/images/icon_clear.png');
      case 'PARTLYCLOUDY':
        return require('../../assets/images/icon_partlycloudy.png');
      case 'CLOUDY':
        return require('../../assets/images/icon_cloudy.png');
      default:
        return require('../../assets/images/icon_default.png');
    }
  };

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

  const hobbiesWeatherData = [
    {hour: '12시', skyType: 'CLEAR', tmp: '25', rain: '0'},
    {hour: '13시', skyType: 'PARTLYCLOUDY', tmp: '26', rain: '0'},
    {hour: '14시', skyType: 'CLOUDY', tmp: '24', rain: '1'},
    {hour: '15시', skyType: 'CLEAR', tmp: '25', rain: '0'},
  ];

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
        <Image source={getWeatherIcon('CLEAR')} style={styles.weatherIcon} />
        <Text style={styles.weatherText}>보통</Text>
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
        {hobbiesWeatherData.map((item, index) => (
          <View key={index} style={styles.timeSlot}>
            <Text style={styles.textTime}>{item.hour}</Text>
            <Image source={getWeatherIcon(item.skyType)} style={styles.icon} />
            <Text style={styles.tmpText}>{`${item.tmp}°C`}</Text>
            <Text style={styles.rainText}>{`${item.rain}mm`}</Text>
          </View>
        ))}
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
    paddingTop: 20,
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
    width: 50,
    height: 50,
    tintColor: '#fff',
  },
  weatherText: {
    color: '#fff',
    fontSize: 18,
    marginTop: 5,
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
    paddingVertical: 30,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 10,
  },
  textTime: {
    color: '#fff',
    fontSize: 13,
    marginBottom: 5,
    textAlign: 'center',
  },
  icon: {
    width: 40,
    height: 40,
    marginBottom: 5,
  },
  tmpText: {
    color: '#fff',
    fontSize: 14,
    marginBottom: 3,
    textAlign: 'center',
  },
  rainText: {
    color: 'skyblue',
    fontSize: 12,
    textAlign: 'center',
  },
});

export default InterestItem;
