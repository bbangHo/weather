import React from 'react';
import {View, Text, Button, Alert, StyleSheet} from 'react-native';
import {login, logout} from '@react-native-seoul/kakao-login';
import {sendAccessTokenToBackend} from '../api/api';

const LoginScreen = ({setIsLoggedIn, setAccessToken}) => {
  const handleLogin = async () => {
    try {
      const token = await login();
      Alert.alert('로그인 성공', `토큰: ${token.accessToken}`);

      const response = await sendAccessTokenToBackend(token.accessToken);

      if (response.isSuccess) {
        setAccessToken(response.result.accessToken);
        setIsLoggedIn(true);
      } else {
        Alert.alert(
          '로그인 실패',
          response.message || '서버 오류가 발생했습니다.',
        );
      }
    } catch (err) {
      Alert.alert('로그인 실패', err.message);
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
      setIsLoggedIn(false);
      setAccessToken(null);
      Alert.alert('로그아웃 성공', '성공적으로 로그아웃되었습니다.');
    } catch (err) {
      Alert.alert('로그아웃 실패', err.message);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>카카오 로그인 테스트</Text>
      <Button title="카카오 로그인" onPress={handleLogin} />
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
