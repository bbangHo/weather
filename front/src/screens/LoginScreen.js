import React, {useState} from 'react';
import {View, Text, Button, Alert, StyleSheet} from 'react-native';
import {KakaoOAuthToken, login} from '@react-native-seoul/kakao-login';

const LoginScreen = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState(null);

  const handleLogin = async () => {
    try {
      const token = await login();
      setIsLoggedIn(true);
      Alert.alert('로그인 성공', `accessToken: ${token.accessToken}`);
    } catch (err) {
      Alert.alert('로그인 실패', err.message);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>카카오 로그인 테스트</Text>
      {!isLoggedIn ? (
        <Button title="카카오 로그인" onPress={handleLogin} />
      ) : (
        <Text style={styles.successText}>로그인 성공!</Text>
      )}
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
  successText: {
    fontSize: 18,
    color: 'green',
  },
});

export default LoginScreen;
