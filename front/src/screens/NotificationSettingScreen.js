import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Platform,
  Alert,
  Linking,
  TouchableOpacity,
  Dimensions,
} from 'react-native';
import {Picker} from '@react-native-picker/picker';
import messaging from '@react-native-firebase/messaging';

const {width, height} = Dimensions.get('window');

const NotificationSettingScreen = () => {
  const [selectedHour, setSelectedHour] = useState('08');
  const [selectedMinute, setSelectedMinute] = useState('00');

  const handleConfirm = async () => {
    try {
      const authStatus = await messaging().hasPermission();
      const enabled =
        authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
        authStatus === messaging.AuthorizationStatus.PROVISIONAL;

      if (!enabled) {
        Alert.alert(
          '알림 권한이 꺼져 있습니다',
          '알림을 받기 위해 설정에서 권한을 허용해주세요.',
          [
            {
              text: '설정으로 가기',
              onPress: () => {
                Linking.openSettings();
              },
            },
            {text: '취소', style: 'cancel'},
          ],
        );
        return;
      }

      Alert.alert(
        '알림 시간 설정',
        `${selectedHour}시 ${selectedMinute}분으로 설정되었습니다.`,
      );

      // 백엔드 전송 로직 추가
    } catch (error) {
      console.error('알림 권한 확인 중 오류:', error);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.headerTitle}>알림 설정</Text>
      <View style={styles.separator} />

      <Text style={styles.title}>알림 시간 설정</Text>
      <View style={styles.pickerContainer}>
        <Picker
          selectedValue={selectedHour}
          onValueChange={value => setSelectedHour(value)}
          style={styles.picker}>
          {Array.from({length: 24}, (_, i) => {
            const hour = i.toString().padStart(2, '0');
            return <Picker.Item key={hour} label={`${hour}시`} value={hour} />;
          })}
        </Picker>
        <Picker
          selectedValue={selectedMinute}
          onValueChange={value => setSelectedMinute(value)}
          style={styles.picker}>
          {['00', '10', '20', '30', '40', '50'].map(min => (
            <Picker.Item key={min} label={`${min}분`} value={min} />
          ))}
        </Picker>
      </View>

      <TouchableOpacity style={styles.button} onPress={handleConfirm}>
        <Text style={styles.buttonText}>확인</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  headerTitle: {
    fontSize: 18,
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
    marginTop: Platform.OS === 'ios' ? height * 0.075 : height * 0.045,
  },
  separator: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
    marginTop: -8,
    marginBottom: 20,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 12,
    marginLeft: 24,
  },
  pickerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 10,
    paddingHorizontal: 24,
  },
  picker: {
    flex: 1,
  },
  button: {
    backgroundColor: '#3f51b5',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 32,
    marginHorizontal: 24,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default NotificationSettingScreen;
