import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Dimensions,
  Linking,
  Alert,
} from 'react-native';

const {width, height} = Dimensions.get('window');

const TermsViewScreen = ({navigation}) => {
  const termsUrls = {
    service:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef804b9169f2b2ee6775dd?pvs=4',
    location:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef80768b0fd53594df6843?pvs=4',
    personalInfo:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef806784c0cc8f3462b80c?pvs=4',
    pushNotification: null,
  };

  const openTermsPage = key => {
    const url = termsUrls[key];
    if (url) {
      Linking.openURL(url).catch(err =>
        console.error('Failed to open URL:', err),
      );
    } else {
      Alert.alert('오류', '해당 약관 페이지를 찾을 수 없습니다.');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>약관 조회</Text>
      <View style={styles.separator} />

      <View style={styles.termsContainer}>
        {[
          {key: 'service', label: '서비스 이용약관'},
          {key: 'location', label: '위치기반서비스 이용약관'},
          {key: 'personalInfo', label: '개인정보처리동의서'},
          {key: 'pushNotification', label: '앱 푸시 알림 수신 동의'},
        ].map(item => (
          <TouchableOpacity
            key={item.key}
            style={styles.termContainer}
            onPress={
              item.key !== 'pushNotification'
                ? () => openTermsPage(item.key)
                : null
            }
            disabled={item.key === 'pushNotification'}>
            <Text style={styles.termText}>{item.label}</Text>
            {item.key !== 'pushNotification' && (
              <Text style={styles.arrow}>&gt;</Text>
            )}
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    paddingHorizontal: 20,
  },
  header: {
    fontSize: 18,
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
    marginTop: Platform.OS === 'ios' ? height * 0.07 : height * 0.045,
  },
  separator: {
    borderBottomWidth: 0.9,
    borderColor: '#E5E5E5',
    marginHorizontal: 15,
    marginTop: -8,
    marginBottom: 20,
  },
  termsContainer: {
    marginBottom: 20,
    marginTop: height * 0.11,
  },
  termContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#E5E5E5',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    marginBottom: 10,
    minHeight: 60,
  },
  termText: {
    fontSize: 14,
    color: '#333',
  },
  arrow: {
    fontSize: 18,
    color: '#ccc',
  },
});

export default TermsViewScreen;
