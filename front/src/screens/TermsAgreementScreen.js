import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Dimensions,
  Alert,
  Linking,
} from 'react-native';
import CheckBox from '@react-native-community/checkbox';
import {registerTermsAgreement} from '../api/api';

const {width, height} = Dimensions.get('window');

const TermsAgreementScreen = ({navigation, route}) => {
  const [agreements, setAgreements] = useState({
    service: false,
    location: false,
    personalInfo: false,
    pushNotification: false,
  });

  const [allChecked, setAllChecked] = useState(false);

  const {accessToken} = route.params;

  const termsUrls = {
    service:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef804b9169f2b2ee6775dd?pvs=4',
    location:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef80768b0fd53594df6843?pvs=4',
    personalInfo:
      'https://safe-scabiosa-656.notion.site/16d2f55997ef806784c0cc8f3462b80c?pvs=4',
    pushNotification: 'https://www.notion.so/ghi789...',
  };

  useEffect(() => {
    const isAllChecked = Object.values(agreements).every(
      value => value === true,
    );
    setAllChecked(isAllChecked);
  }, [agreements]);

  const toggleAll = newValue => {
    setAllChecked(newValue);
    setAgreements({
      service: newValue,
      location: newValue,
      personalInfo: newValue,
      pushNotification: newValue,
    });
  };

  const toggleAgreement = key => {
    setAgreements(prevState => ({
      ...prevState,
      [key]: !prevState[key],
    }));
  };

  const handleComplete = async () => {
    if (
      !agreements.service ||
      !agreements.location ||
      !agreements.personalInfo
    ) {
      Alert.alert('약관 동의', '필수 약관에 동의해주세요.');
      return;
    }

    try {
      const response = await registerTermsAgreement(accessToken, {
        isServiceTermsAgreed: agreements.service,
        isPrivacyPolicyAgreed: agreements.personalInfo,
        isLocationServiceTermsAgreed: agreements.location,
        isPushNotificationAgreed: agreements.pushNotification,
      });

      console.log('약관 동의 완료:', response);

      navigation.navigate('RegisterProfileScreen', {
        accessToken,
      });
    } catch (error) {
      console.error('약관 동의 실패:', error);
      Alert.alert(
        '약관 동의 실패',
        error.message || '약관 동의 중 오류가 발생했습니다.',
      );
    }
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
      <Text style={styles.header}>약관 동의</Text>
      <View style={styles.separator} />

      <View style={styles.termsContainer}>
        {[
          {key: 'service', label: '서비스 이용약관', required: true},
          {key: 'location', label: '위치기반서비스 이용약관', required: true},
          {key: 'personalInfo', label: '개인정보처리동의서', required: true},
          {
            key: 'pushNotification',
            label: '앱 푸시 알림 수신 동의',
            required: false,
          },
        ].map(item => (
          <View key={item.key} style={styles.termContainer}>
            <View style={styles.term}>
              <CheckBox
                value={agreements[item.key]}
                onValueChange={() => toggleAgreement(item.key)}
                tintColors={{true: '#3f7dfd', false: '#ccc'}}
              />
              <Text style={styles.termText}>
                {item.label}{' '}
                {item.required ? (
                  <View style={styles.requiredContainer}>
                    <Text style={styles.required}>필수</Text>
                  </View>
                ) : (
                  <View style={styles.optionalContainer}>
                    <Text style={styles.optional}>선택</Text>
                  </View>
                )}
              </Text>
            </View>
            {item.key !== 'pushNotification' && (
              <TouchableOpacity onPress={() => openTermsPage(item.key)}>
                <Text style={styles.arrow}>&gt;</Text>
              </TouchableOpacity>
            )}
          </View>
        ))}
      </View>

      <View style={styles.allAgreeContainer}>
        <View style={styles.allAgree}>
          <CheckBox
            value={allChecked}
            onValueChange={toggleAll}
            tintColors={{true: '#2f5af4', false: '#ccc'}}
          />
          <Text style={styles.allAgreeText}>전체 동의하기</Text>
        </View>
        <Text style={styles.allAgreeDescription}>모든 약관에 동의합니다</Text>
      </View>

      <TouchableOpacity style={styles.completeButton} onPress={handleComplete}>
        <Text style={styles.completeButtonText}>완료</Text>
      </TouchableOpacity>
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
  },
  term: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  termText: {
    fontSize: 14,
    marginLeft: 10,
    color: '#333',
    marginBottom: Platform.OS === 'ios' ? 0 : 7,
  },
  requiredContainer: {
    backgroundColor: '#EFF6FF',
    borderRadius: 4,
    paddingHorizontal: 4,
    paddingVertical: 2,
    marginLeft: Platform.OS === 'ios' ? 5 : 9,
    marginTop: Platform.OS === 'ios' ? -2 : -10,
  },
  optionalContainer: {
    backgroundColor: '#F3F4F6',
    borderRadius: 4,
    paddingHorizontal: 4,
    paddingVertical: 2,
    marginLeft: 5,
    marginTop: -2,
  },
  required: {
    color: '#1D4ED8',
    fontSize: 12,
  },
  optional: {
    color: '#6B7280',
    fontSize: 12,
  },
  arrow: {
    fontSize: 18,
    color: '#ccc',
  },
  allAgreeContainer: {
    flexDirection: 'column',
    justifyContent: 'center',
    borderWidth: 1,
    borderColor: '#E5E5E5',
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 12,
    marginTop: height * 0.05,
  },
  allAgree: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  allAgreeText: {
    fontSize: 16,
    marginLeft: 10,
    color: '#333',
  },
  allAgreeDescription: {
    fontSize: 12,
    color: '#6B7280',
    marginTop: 5,
    marginLeft: width * 0.11,
    marginBottom: Platform.OS === 'ios' ? 3 : 5,
  },
  completeButton: {
    marginTop: height * 0.08,
    backgroundColor: '#2f5af4',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  completeButtonText: {
    fontSize: 16,
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default TermsAgreementScreen;
