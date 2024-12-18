import React, {useEffect, useState} from 'react';
import {
  ScrollView,
  Text,
  View,
  StyleSheet,
  Image,
  TouchableOpacity,
  Alert,
  Dimensions,
} from 'react-native';
import {fetchMemberInfo, deleteMember} from '../api/api';
import profilePlaceholder from '../../assets/images/profile.png';
import loadingIcon from '../../assets/images/icon_loading.png';
import {logout} from '@react-native-seoul/kakao-login';
import AsyncStorage from '@react-native-async-storage/async-storage';

const {width, height} = Dimensions.get('window');

const MyScreen = ({
  accessToken,
  setIsNewMember,
  setLocationId,
  setIsLoggedIn,
  setAccessToken,
  navigation,
}) => {
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
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
        setNickname(memberInfo.nickname || '닉네임');
        setEmail(memberInfo.email || 'example@email.com');
        setLoading(false);
      } catch (error) {
        console.error('회원 정보 불러오는 중 오류 발생:', error);
        setLoading(false);
      }
    };

    loadMemberInfo();
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
    <ScrollView style={styles.container}>
      <Text style={styles.headerTitle}>마이페이지</Text>
      <View style={styles.separatorMy} />

      <View style={styles.profileSection}>
        <Image source={profileImage} style={styles.profileImage} />
      </View>

      <View style={styles.profileInfoContainer}>
        <View style={styles.profileTextContainer}>
          <Text style={styles.nickname}>{nickname}</Text>
          <Text style={styles.email}>{email}</Text>
        </View>
        <TouchableOpacity onPress={() => navigation.navigate('ProfileScreen')}>
          <Text style={styles.profileEdit}>프로필 수정</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.separator} />
      <View style={styles.menuSection}>
        <TouchableOpacity style={styles.menuItem}>
          <Text style={styles.menuText}>알림 설정</Text>
          <Text style={styles.menuArrow}>{'>'}</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.menuItem}>
          <Text style={styles.menuText}>이용약관</Text>
          <Text style={styles.menuArrow}>{'>'}</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.menuItem}>
          <Text style={styles.menuText}>개인정보 처리방침</Text>
          <Text style={styles.menuArrow}>{'>'}</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.separator} />
      <View style={styles.footerSection}>
        <View style={styles.appInfo}>
          <Text style={styles.footerText}>앱 버전</Text>
          <Text style={styles.footerValue}>1.0.0</Text>
        </View>
        <TouchableOpacity onPress={handleDeleteAccount} style={styles.menuItem}>
          <Text style={styles.menuText}>회원탈퇴</Text>
          <Text style={styles.menuArrow}>{'>'}</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.separator} />
      <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
        <Text style={styles.logoutText}>로그아웃</Text>
      </TouchableOpacity>
    </ScrollView>
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
    marginTop: height * 0.07,
  },
  profileSection: {
    alignItems: 'center',
    marginBottom: 10,
  },
  profileImage: {
    width: width * 0.3,
    height: width * 0.3,
    borderRadius: 9999,
    backgroundColor: '#e0e0e0',
    marginTop: height * 0.01,
  },
  profileInfoContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    marginBottom: 10,
  },
  profileTextContainer: {
    flexDirection: 'column',
  },
  nickname: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#000',
    marginBottom: 3,
    marginTop: height * 0.05,
  },
  email: {
    fontSize: 14,
    color: '#777',
    marginTop: height * 0.01,
    marginBottom: height * 0.01,
  },
  profileEdit: {
    fontSize: 14,
    color: '#777',
    fontWeight: 'bold',
  },
  menuSection: {
    marginTop: 10,
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 15,
    paddingHorizontal: 20,
  },
  menuText: {
    fontSize: 16,
    color: '#333',
  },
  menuArrow: {
    fontSize: 16,
    color: '#ccc',
    fontWeight: 'bold',
  },
  separator: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
  },
  separatorMy: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
    marginTop: -8,
    marginBottom: 20,
  },
  footerSection: {
    marginTop: 10,
  },
  appInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 15,
    paddingHorizontal: 20,
  },
  footerText: {
    fontSize: 16,
    color: '#333',
  },
  footerValue: {
    fontSize: 16,
    color: '#666',
  },
  logoutButton: {
    marginTop: 40,
    marginHorizontal: 20,
    backgroundColor: '#F2F3F5',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  logoutText: {
    fontSize: 16,
    color: '#000',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingIcon: {
    width: 40,
    height: 40,
  },
});

export default MyScreen;
