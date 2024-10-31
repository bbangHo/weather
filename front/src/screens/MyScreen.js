import React, {useState, useEffect} from 'react';
import {View, Text, StyleSheet, Image} from 'react-native';
import {fetchMemberInfo} from '../api/api';
import profilePlaceholder from '../../assets/images/profile.png';

const MyScreen = ({accessToken, setIsNewMember, setLocationId}) => {
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

  if (loading) {
    return <Text>회원 정보를 불러오는 중...</Text>;
  }

  return (
    <View style={styles.container}>
      <View style={styles.profileContainer}>
        <Image source={profileImage} style={styles.profileImage} />
      </View>
      <Text style={styles.label}>닉네임</Text>
      <View style={styles.infoTextContainer}>
        <Text style={styles.infoText}>{nickname}</Text>
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
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#fff',
  },
  profileContainer: {
    marginTop: 50,
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
    fontSize: 18,
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
  infoText: {
    fontSize: 16,
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
});

export default MyScreen;
