import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  Alert,
  StyleSheet,
  TextInput,
  TouchableHighlight,
  Platform,
  PermissionsAndroid,
  Linking,
} from 'react-native';
import {login} from '@react-native-seoul/kakao-login';
import AsyncStorage from '@react-native-async-storage/async-storage';
import appleAuth, {
  AppleButton,
} from '@invertase/react-native-apple-authentication';
import Geolocation from 'react-native-geolocation-service';
import {
  sendAccessTokenToBackend,
  sendLocationToBackend,
  refreshAccessToken,
} from '../api/api';

const LoginScreen = ({
  setIsLoggedIn,
  setAccessToken,
  setIsNewMember,
  navigation,
}) => {
  const [token, setToken] = useState('');

  const handleSignInApple = async () => {
    try {
      const appleAuthRequestResponse = await appleAuth.performRequest({
        requestedOperation: appleAuth.Operation.LOGIN,
        requestedScopes: [appleAuth.Scope.FULL_NAME, appleAuth.Scope.EMAIL],
      });

      const credentialState = await appleAuth.getCredentialStateForUser(
        appleAuthRequestResponse.user,
      );

      if (credentialState === appleAuth.State.AUTHORIZED) {
        console.log('Apple Login Successful:', appleAuthRequestResponse);

        console.log('Identity Token:', appleAuthRequestResponse.identityToken);
        console.log('User ID:', appleAuthRequestResponse.user);
        console.log('Full Name:', appleAuthRequestResponse.fullName);
        console.log('Email:', appleAuthRequestResponse.email);

        Alert.alert('애플 로그인 성공', '로그를 확인해주세요.');
      } else {
        Alert.alert('애플 로그인 실패', '사용자가 인증되지 않았습니다.');
      }
    } catch (err) {
      console.error('Apple Login Error:', err.message);
      Alert.alert(
        '애플 로그인 실패',
        err.message || '알 수 없는 오류가 발생했습니다.',
      );
    }
  };

  const handleKakaoLogin = async () => {
    try {
      console.log('Starting Kakao login...');
      const token = await login();
      console.log('Kakao login successful, token:', token.accessToken);
      const response = await sendAccessTokenToBackend(token.accessToken);
      if (response.isSuccess) {
        console.log('Login successful, server response:', response);
        setAccessToken(response.result.accessToken);
        // 테스트를 위해 false 값으로 설정합니다.
        // 구현 완료 후 true 값으로 변경해야 합니다.
        setIsNewMember(response.result.isNewMember === 'true');
        setIsLoggedIn(true);

        await AsyncStorage.setItem('accessToken', response.result.accessToken);
        await AsyncStorage.setItem(
          'refreshToken',
          response.result.refreshToken,
        );
      } else {
        console.error('Login failed, server response:', response);
        Alert.alert(
          '로그인 실패',
          response.message || '서버 오류가 발생했습니다.',
        );
      }
    } catch (err) {
      console.error('Kakao Login Failed:', err.message);
      Alert.alert('로그인 실패', err.message);
    }
  };

  const handleRegularLogin = async () => {
    try {
      if (!token) {
        Alert.alert('토큰 입력 오류', '테스트 토큰을 입력해주세요.');
        return;
      }

      setAccessToken(token);
      setIsLoggedIn(true);
      setIsNewMember(false);

      await AsyncStorage.setItem('accessToken', token);

      Alert.alert(
        '로그인 성공',
        '테스트 토큰으로 성공적으로 로그인되었습니다.',
      );

      const permissionGranted = await requestLocationPermission();
      if (permissionGranted) {
        await getCurrentLocation(token);
      } else {
        Alert.alert(
          '위치 권한 필요',
          '위치 정보를 등록하려면 권한을 허용해주세요. 앱 설정에서 권한을 활성화하세요.',
          [
            {text: '취소', style: 'cancel'},
            {
              text: '설정 열기',
              onPress: async () => {
                try {
                  await Linking.openSettings();
                } catch (error) {
                  console.error('Error opening settings:', error);
                  Alert.alert('오류', '설정을 열 수 없습니다.');
                }
              },
            },
          ],
        );
      }
    } catch (err) {
      console.error('로그인 실패:', err.message);
      Alert.alert('로그인 실패', err.message);
    }
  };

  const requestLocationPermission = async () => {
    try {
      if (Platform.OS === 'ios') {
        const status = await Geolocation.requestAuthorization('whenInUse');
        return status === 'granted';
      } else {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
          {
            title: '위치 접근 권한',
            message: '앱에서 위치 정보를 사용하려면 권한이 필요합니다.',
            buttonNeutral: '나중에',
            buttonNegative: '취소',
            buttonPositive: '허용',
          },
        );
        return granted === PermissionsAndroid.RESULTS.GRANTED;
      }
    } catch (error) {
      console.error('Error requesting location permission:', error);
      return false;
    }
  };

  const getCurrentLocation = async token => {
    Geolocation.getCurrentPosition(
      async position => {
        const {longitude, latitude} = position.coords;
        try {
          const response = await sendLocationToBackend(
            longitude,
            latitude,
            token,
          );
          console.log('Location registered successfully:', response);
          Alert.alert(
            '위치 등록 완료',
            '위치 정보가 성공적으로 등록되었습니다.',
          );
        } catch (error) {
          console.error('Error registering location:', error);
          Alert.alert(
            '위치 등록 실패',
            '위치 정보를 등록하는 중 오류가 발생했습니다.',
          );
        }
      },
      error => {
        console.error('Error getting current position:', error);
        Alert.alert(
          '위치 정보를 가져올 수 없습니다.',
          '위치 권한을 확인해주세요.',
        );
      },
      {enableHighAccuracy: true, timeout: 15000, maximumAge: 10000},
    );
  };

  useEffect(() => {
    const refreshTokenImmediately = async () => {
      try {
        console.log('Attempting to refresh token immediately...');
        const newAccessToken = await refreshAccessToken();
        console.log('Refreshed access token immediately:', newAccessToken);

        setAccessToken(newAccessToken);
        setIsLoggedIn(true);
      } catch (err) {
        console.error('Failed to refresh token immediately:', err);
        handleLogout();
      }
    };

    refreshTokenImmediately();

    const interval = setInterval(async () => {
      try {
        console.log('Attempting to refresh token...');
        const newAccessToken = await refreshAccessToken();
        console.log('Refreshed access token:', newAccessToken);
        setAccessToken(newAccessToken);
      } catch (err) {
        console.error('Failed to refresh token:', err);
        handleLogout();
      }
    }, 15 * 60 * 1000);
    return () => clearInterval(interval);
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>임시 로그인</Text>

      <TextInput
        style={styles.input}
        placeholder="테스트 계정 토큰을 입력해 주세요."
        value={token}
        onChangeText={setToken}
        autoCapitalize="none"
        multiline={true}
        placeholderTextColor="#000"
      />

      <TouchableHighlight
        style={styles.loginButton}
        onPress={handleRegularLogin}>
        <Text style={styles.loginButtonText}>로그인</Text>
      </TouchableHighlight>

      <TouchableHighlight style={styles.kakaoButton} onPress={handleKakaoLogin}>
        <Text style={styles.kakaoButtonText}>카카오톡으로 로그인</Text>
      </TouchableHighlight>

      {Platform.OS === 'ios' && (
        <AppleButton
          buttonStyle={AppleButton.Style.WHITE}
          buttonType={AppleButton.Type.SIGN_IN}
          style={{
            width: '100%',
            height: 45,
            marginTop: 20,
          }}
          onPress={handleSignInApple}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 16,
    marginBottom: 20,
    color: '#000',
  },
  input: {
    width: '100%',
    padding: 10,
    borderWidth: 1,
    borderColor: '#ccc',
    marginBottom: 10,
    borderRadius: 5,
    height: 80,
    textAlignVertical: 'top',
  },
  kakaoButton: {
    backgroundColor: '#FEE500',
    paddingVertical: 10,
    paddingHorizontal: 15,
    borderRadius: 5,
    alignItems: 'center',
    width: '100%',
    marginTop: 70,
  },
  kakaoButtonText: {
    color: '#3C1E1E',
    fontSize: 16,
    fontWeight: 'bold',
  },
  loginButton: {
    backgroundColor: '#0066cc',
    paddingVertical: 10,
    paddingHorizontal: 15,
    borderRadius: 5,
    alignItems: 'center',
    width: '100%',
    marginTop: 10,
  },
  loginButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default LoginScreen;
