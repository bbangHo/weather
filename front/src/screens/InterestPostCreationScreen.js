import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Alert,
} from 'react-native';
import {createInterestPost} from '../api/api';

const InterestPostCreationScreen = ({
  navigation,
  accessToken,
  memberId,
  locationId,
}) => {
  const [content, setContent] = useState('');
  const [postType, setPostType] = useState(null);

  useEffect(() => {
    console.log('Location ID:', locationId);
  }, [locationId, accessToken, memberId]);

  const handleSubmit = async () => {
    if (!postType) {
      Alert.alert('Error', '먼저 취미를 선택해 주세요.');
      return;
    }

    if (!locationId) {
      Alert.alert('Error', '위치 정보가 없습니다. 다시 시도해 주세요.');
      return;
    }

    const postData = {
      content,
      postType,
      locationId,
    };

    console.log('Requesting with postData:', postData);
    console.log('accessToken:', accessToken, 'memberId:', memberId);

    try {
      const response = await createInterestPost(
        postData,
        accessToken,
        memberId,
      );
      console.log('Post creation response:', response);
      Alert.alert('Success', '게시글이 성공적으로 등록되었습니다.');
      navigation.navigate('InterestScreen');
    } catch (error) {
      console.error('Error creating interest post:', error);
      Alert.alert('Error', '게시글 등록에 실패했습니다. 다시 시도해 주세요.');
    }
  };

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.contentContainer}>
      <Text style={styles.label}>취미를 선택해 주세요</Text>
      <View style={styles.hobbyContainer}>
        <TouchableOpacity
          style={[
            styles.hobbyButton,
            postType === 'RUN' && styles.selectedButton,
          ]}
          onPress={() => setPostType('RUN')}>
          <Text
            style={[
              styles.hobbyButtonText,
              postType === 'RUN' && styles.selectedButtonText,
            ]}>
            런닝
          </Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[
            styles.hobbyButton,
            postType === 'HIKING' && styles.selectedButton,
          ]}
          onPress={() => setPostType('HIKING')}>
          <Text
            style={[
              styles.hobbyButtonText,
              postType === 'HIKING' && styles.selectedButtonText,
            ]}>
            등산
          </Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[
            styles.hobbyButton,
            postType === 'PET' && styles.selectedButton,
          ]}
          onPress={() => setPostType('PET')}>
          <Text
            style={[
              styles.hobbyButtonText,
              postType === 'PET' && styles.selectedButtonText,
            ]}>
            반려동물 산책
          </Text>
        </TouchableOpacity>
      </View>

      <Text style={styles.label}>날씨는 어떤가요?</Text>
      <TextInput
        style={styles.textInput}
        placeholder="날씨와 취미를 공유해 주세요"
        placeholderTextColor="#888"
        multiline
        maxLength={500}
        value={content}
        onChangeText={setContent}
      />

      <TouchableOpacity style={styles.shareButton} onPress={handleSubmit}>
        <Text style={styles.shareButtonText}>공유하기</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  contentContainer: {
    padding: 16,
    paddingBottom: 30,
    paddingTop: 130,
  },
  label: {
    fontSize: 16,
    marginBottom: 10,
  },
  textInput: {
    paddingTop: 12,
    paddingLeft: 12,
    fontSize: 16,
    height: 300,
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 8,
    paddingHorizontal: 10,
    marginBottom: 20,
    textAlignVertical: 'top',
    color: '#000',
  },
  hobbyContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 50,
  },
  hobbyButton: {
    flex: 1,
    paddingVertical: 20,
    borderColor: '#2f5af4',
    borderWidth: 1,
    borderRadius: 8,
    alignItems: 'center',
    marginHorizontal: 5,
  },
  hobbyButtonText: {
    color: '#2f5af4',
    fontSize: 16,
  },
  selectedButton: {
    backgroundColor: '#2f5af4',
  },
  selectedButtonText: {
    color: '#fff',
  },
  shareButton: {
    paddingVertical: 12,
    backgroundColor: '#2f5af4',
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 20,
  },
  shareButtonText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default InterestPostCreationScreen;
