import React, {useState} from 'react';
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
import {sendAccessTokenToBackend} from '../api/api';

const LoginScreen = ({
  setIsLoggedIn,
  setAccessToken,
  setIsNewMember,
  navigation,
}) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleKakaoLogin = async () => {
    try {
      console.log('Starting Kakao login...');
      const token = await login();
      console.log('Kakao login successful, token:', token.accessToken);
      Alert.alert('로그인 성공', `토큰: ${token.accessToken}`);
      const response = await sendAccessTokenToBackend(token.accessToken);
      if (response.isSuccess) {
        setAccessToken(response.result.accessToken);
        setIsNewMember(response.result.isNewMember === 'true');
        setIsLoggedIn(true);

        await AsyncStorage.setItem('accessToken', response.result.accessToken);
        await AsyncStorage.setItem(
          'refreshToken',
          response.result.refreshToken,
        );
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

  const handleRegularLogin = async () => {
    try {
      const token =
        'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RlckB0ZXN0LmNvbSIsImlkIjoxMDAyLCJpYXQiOjE3MzA5MTMxNDQsImV4cCI6NDg4NDUxMzE0NH0.F__4oJACAY85nKT-oyjAargtjVKpmsHn6MeIEYDxzjI';
      const response = await sendAccessTokenToBackend(token);
      if (response.isSuccess) {
        setAccessToken(response.result.accessToken);
        setIsNewMember(response.result.isNewMember === 'true');
        setIsLoggedIn(true);
        Alert.alert('로그인 성공', '일반 로그인에 성공했습니다.');

        await AsyncStorage.setItem('accessToken', response.result.accessToken);
        await AsyncStorage.setItem(
          'refreshToken',
          response.result.refreshToken,
        );
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

  return (
    <View style={styles.container}>
      <Text style={styles.title}>로그인</Text>

      <TextInput
        style={styles.input}
        placeholder="이메일"
        value={email}
        onChangeText={setEmail}
        keyboardType="email-address"
        autoCapitalize="none"
      />
      <TextInput
        style={styles.input}
        placeholder="비밀번호"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />

      <TouchableHighlight
        style={styles.loginButton}
        onPress={handleRegularLogin}>
        <Text style={styles.loginButtonText}>로그인</Text>
      </TouchableHighlight>

      <TouchableOpacity
        onPress={() => navigation.navigate('RegisterProfileScreen')}>
        <Text style={styles.signupText}>회원이 아니신가요? 회원가입하기</Text>
      </TouchableOpacity>

      <Text style={styles.kakaoTitle}>간편 로그인</Text>
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
    fontSize: 24,
    marginBottom: 20,
  },
  input: {
    width: '100%',
    padding: 10,
    borderWidth: 1,
    borderColor: '#ccc',
    marginBottom: 10,
    borderRadius: 5,
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
