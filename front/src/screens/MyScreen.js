import React, {useEffect, useState} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  Image,
  Alert,
  TouchableOpacity,
  Platform,
} from 'react-native';
import {fetchMemberInfo, deleteMember, refreshAccessToken} from '../api/api';
import profilePlaceholder from '../../assets/images/profile.png';
import loadingIcon from '../../assets/images/icon_loading.png';
import {logout} from '@react-native-seoul/kakao-login';
import AsyncStorage from '@react-native-async-storage/async-storage';

const MyScreen = ({
  accessToken,
  setIsNewMember,
  setLocationId,
  setIsLoggedIn,
  setAccessToken,
}) => {
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
  const [selectedType, setSelectedType] = useState(null);
  const [address, setAddress] = useState('');
  const [profileImage, setProfileImage] = useState(profilePlaceholder);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadMemberInfo = async () => {
      try {
        const memberInfo = await fetchMemberInfo(accessToken);
        console.log('Fetched member info:', memberInfo);

        if (
          memberInfo.profileImage &&
          memberInfo.profileImage.startsWith('http')
        ) {
          setProfileImage({uri: memberInfo.profileImage});
        } else {
          setProfileImage(profilePlaceholder);
        }

        setNickname(memberInfo.nickname || '');
        setEmail(memberInfo.email || '');

        switch (memberInfo.sensitivity) {
          case 'HOT':
            setSelectedType('hot');
            break;
          case 'NONE':
            setSelectedType('normal');
            break;
          case 'COLD':
            setSelectedType('cold');
            break;
          default:
            setSelectedType(null);
            break;
        }

        setAddress(
          `${memberInfo.province} ${memberInfo.city} ${memberInfo.street}`,
        );
        setLocationId(memberInfo.locationId);
        setLoading(false);
      } catch (error) {
        console.error('회원 정보 불러오는 중 오류 발생:', error);
        setLoading(false);
      }
    };

    if (accessToken) {
      loadMemberInfo();
    }
  }, [accessToken]);

  const handleLogout = async () => {
    try {
      console.log('Starting Kakao logout...');
      await logout();
      setIsLoggedIn(false);
      setAccessToken(null);
      await AsyncStorage.removeItem('accessToken');
      await AsyncStorage.removeItem('refreshToken');
      Alert.alert('로그아웃 성공', '성공적으로 로그아웃되었습니다.');
    } catch (err) {
      console.error('Logout failed:', err.message);
      Alert.alert('로그아웃 실패', '이미 로그아웃된 상태입니다.');
      setIsLoggedIn(false);
      setAccessToken(null);
      await AsyncStorage.removeItem('accessToken');
      await AsyncStorage.removeItem('refreshToken');
    }
  };

  const handleDeleteAccount = async () => {
    if (!accessToken) {
      Alert.alert('오류', '유효한 인증 토큰이 없습니다.');
      return;
    }

    const ensureAccessToken = async () => {
      try {
        const newAccessToken = await refreshAccessToken(accessToken);
        if (newAccessToken) {
          console.log('New access token:', newAccessToken);
          await AsyncStorage.setItem('accessToken', newAccessToken);
          setAccessToken(newAccessToken);
          return newAccessToken;
        } else {
          console.log('Access token remains the same:', accessToken);
          return accessToken;
        }
      } catch (error) {
        console.error('Failed to refresh access token:', error);
        Alert.alert('오류', '인증이 만료되었습니다. 다시 로그인해주세요.', [
          {text: '확인', onPress: handleLogout},
        ]);
        throw error;
      }
    };

    Alert.alert(
      '회원 탈퇴',
      '정말 탈퇴하시겠습니까?',
      [
        {text: '아니오', style: 'cancel'},
        {
          text: '네',
          onPress: async () => {
            try {
              const validAccessToken = await ensureAccessToken();
              await deleteMember(validAccessToken);
              Alert.alert('탈퇴 완료', '회원 탈퇴가 완료되었습니다.');
              setIsNewMember(true);
              setIsLoggedIn(false);
            } catch (error) {
              console.error('Failed to delete member:', error);
              Alert.alert(
                '오류',
                '회원 탈퇴에 실패했습니다. 다시 시도해주세요.',
              );
            }
          },
        },
      ],
      {cancelable: true},
    );
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <Image source={loadingIcon} style={styles.loadingIcon} />
      </View>
    );
  }

  return (
    <ScrollView style={styles.scrollContainer}>
      <View style={styles.container}>
        <View style={styles.profileContainer}>
          <Image source={profileImage} style={styles.profileImage} />
        </View>
        <Text style={styles.label}>닉네임</Text>
        <View style={styles.infoTextContainer}>
          <Text style={styles.infoTextNickname}>{nickname}</Text>
        </View>

        <Text style={styles.label}>대표 주소</Text>
        <Text style={styles.addressText}>{address}</Text>

        <Text style={styles.label}>이메일</Text>
        <Text style={styles.infoText}>{email}</Text>

        <Text style={styles.label}>유형</Text>
        <View
          style={[
            styles.typeButton,
            selectedType === 'hot' && styles.selectedButton,
          ]}>
          <Text
            style={[
              styles.typeButtonText,
              selectedType === 'hot' && styles.selectedButtonText,
            ]}>
            더위를 많이 타는 편
          </Text>
        </View>
        <View
          style={[
            styles.typeButton,
            selectedType === 'normal' && styles.selectedButton,
          ]}>
          <Text
            style={[
              styles.typeButtonText,
              selectedType === 'normal' && styles.selectedButtonText,
            ]}>
            평범한 편
          </Text>
        </View>
        <View
          style={[
            styles.typeButton,
            selectedType === 'cold' && styles.selectedButton,
          ]}>
          <Text
            style={[
              styles.typeButtonText,
              selectedType === 'cold' && styles.selectedButtonText,
            ]}>
            추위를 많이 타는 편
          </Text>
        </View>

        <View style={styles.buttonContainer}>
          <TouchableOpacity onPress={handleLogout}>
            <Text style={styles.logoutButtonText}>로그아웃</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={handleDeleteAccount}>
            <Text style={styles.deleteButtonText}>회원 탈퇴하기</Text>
          </TouchableOpacity>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  scrollContainer: {
    flex: 1,
    backgroundColor: '#fff',
  },
  container: {
    alignItems: 'center',
    padding: 20,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  loadingIcon: {
    width: 40,
    height: 40,
  },
  profileContainer: {
    marginTop: Platform.OS === 'ios' ? 50 : 25,
    alignItems: 'center',
    justifyContent: 'center',
    position: 'relative',
  },
  profileImage: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: '#e0e0e0',
  },
  label: {
    fontSize: 17,
    marginTop: 30,
    marginBottom: 10,
    color: '#333',
    fontWeight: 'bold',
  },
  infoTextContainer: {
    width: '100%',
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    paddingBottom: 8,
    marginBottom: 10,
  },
  addressText: {
    fontSize: 16,
    color: '#555',
    marginBottom: 10,
  },
  infoTextNickname: {
    fontSize: 15,
    color: '#333',
    marginBottom: 5,
    marginLeft: 5,
  },
  infoText: {
    fontSize: 15,
    color: '#333',
    marginBottom: 10,
  },
  typeButton: {
    width: '100%',
    padding: 14,
    borderWidth: 1,
    borderColor: '#2f5af4',
    borderRadius: 5,
    alignItems: 'center',
    marginVertical: 5,
    backgroundColor: '#fff',
    paddingBottom: Platform.OS === 'ios' ? 13 : 16,
  },
  typeButtonText: {
    color: '#2f5af4',
    fontSize: 16,
  },
  selectedButton: {
    backgroundColor: '#2f5af4',
  },
  selectedButtonText: {
    color: '#fff',
  },
  buttonContainer: {
    flexDirection: 'row',
    marginTop: 30,
    justifyContent: 'space-between',
    width: '60%',
  },
  logoutButtonText: {
    fontSize: 13,
    color: 'gray',
    textDecorationLine: 'underline',
  },
  deleteButtonText: {
    fontSize: 13,
    color: 'gray',
    textDecorationLine: 'underline',
  },
});

export default MyScreen;
