import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  Alert,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  TouchableHighlight,
} from 'react-native';
import {login} from '@react-native-seoul/kakao-login';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {sendAccessTokenToBackend, refreshAccessToken} from '../api/api';

const LoginScreen = ({
  setIsLoggedIn,
  setAccessToken,
  setIsNewMember,
  navigation,
}) => {
  const [token, setToken] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

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
      console.error('Login failed:', err.message);
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
    } catch (err) {
      Alert.alert('로그인 실패', err.message);
    }
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
      />

      <TouchableHighlight
        style={styles.loginButton}
        onPress={handleRegularLogin}>
        <Text style={styles.loginButtonText}>로그인</Text>
      </TouchableHighlight>

      <TouchableHighlight style={styles.kakaoButton} onPress={handleKakaoLogin}>
        <Text style={styles.kakaoButtonText}>카카오톡으로 로그인</Text>
      </TouchableHighlight>
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
  signupText: {
    marginTop: 10,
    textDecorationLine: 'underline',
  },
  kakaoTitle: {
    fontSize: 16,
    marginVertical: 15,
    marginTop: 50,
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
