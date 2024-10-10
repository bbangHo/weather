import React, {useState} from 'react';
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
import Icon from 'react-native-vector-icons/Ionicons';

const InterestItem = ({
  weatherData,
  selectedHobby,
  setSelectedHobby,
  modalVisible,
  setModalVisible,
}) => {
  const handleHobbyPress = hobby => {
    setSelectedHobby(hobby);
  };

  const handleApply = () => {
    setModalVisible(false);
  };

  const handleLocationPress = () => {
    console.log('Location pressed!');
  };

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
    {id: 1, name: '산책', icon: 'paw-outline'},
    {id: 2, name: '스키', icon: 'snow-outline'},
    {id: 3, name: '테니스', icon: 'tennisball-outline'},
    {id: 4, name: '낚시', icon: 'fish-outline'},
    {id: 5, name: '등산', icon: 'walk-outline'},
    {id: 6, name: '등등', icon: 'ellipsis-horizontal'},
  ];

  const hobbiesWeatherData = [
    {hour: '12시', skyType: 'CLEAR', tmp: '25', rain: '0'},
    {hour: '13시', skyType: 'PARTLYCLOUDY', tmp: '26', rain: '0'},
    {hour: '14시', skyType: 'CLOUDY', tmp: '24', rain: '1'},
    {hour: '15시', skyType: 'CLEAR', tmp: '25', rain: '0'},
    {hour: '등등', skyType: 'PARTLYCLOUDY', tmp: '26', rain: '0'},
  ];

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={handleLocationPress}>
        <Text style={styles.locationText}>강원도 정선군 ▼</Text>
      </TouchableOpacity>
      <View style={styles.weatherIconContainer}>
        <Icon name="cloud-outline" size={50} color="#fff" />
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
          <View style={styles.modalContainer}>
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
                  <Icon name={item.icon} size={30} color="#3f51b5" />
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
            <TouchableOpacity style={styles.applyButton} onPress={handleApply}>
              <Text style={styles.applyText}>적용하기</Text>
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
    width: width * 0.25,
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
  hobbyList: {
    alignItems: 'center',
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
