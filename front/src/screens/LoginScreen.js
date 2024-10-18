import React from 'react';
import {View, Text, Button, Alert, StyleSheet} from 'react-native';
import {login, logout} from '@react-native-seoul/kakao-login';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {sendAccessTokenToBackend} from '../api/api';

const LoginScreen = ({setIsLoggedIn, setAccessToken, setIsNewMember}) => {
  const handleLogin = async () => {
    try {
      console.log('Starting Kakao login...');
      const token = await login();

      console.log('Kakao login successful, token:', token.accessToken);
      Alert.alert('로그인 성공', `토큰: ${token.accessToken}`);

      const response = await sendAccessTokenToBackend(token.accessToken);

      if (response.isSuccess) {
        console.log('Login successful, server response:', response);

        setAccessToken(response.result.accessToken);
        // 테스트를 위해 false 값으로 설정합니다.
        // 구현 완료 후 true 값으로 변경해야 합니다.
        setIsNewMember(response.result.isNewMember === 'false');
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

  const handleLogout = async () => {
    try {
      console.log('Starting Kakao logout...');
      await logout();
      setIsLoggedIn(false);
      setAccessToken(null);

      await AsyncStorage.removeItem('accessToken');
      await AsyncStorage.removeItem('refreshToken');

      console.log('Logout successful');
      Alert.alert('로그아웃 성공', '성공적으로 로그아웃되었습니다.');
    } catch (err) {
      console.error('Logout failed:', err.message);
      Alert.alert('로그아웃 실패', err.message);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>카카오 로그인 테스트</Text>
      <Button title="카카오 로그인" onPress={handleLogin} />
      <Button title="카카오 로그아웃" onPress={handleLogout} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    marginBottom: 20,
  },
});

export default LoginScreen;
