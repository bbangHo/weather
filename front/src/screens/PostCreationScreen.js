import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  ScrollView,
} from 'react-native';
import {createPost} from '../api/api';

const PostCreationScreen = ({navigation, accessToken, memberId}) => {
  const [temperature, setTemperature] = useState(null);
  const [weather, setWeather] = useState(null);
  const [humidity, setHumidity] = useState(null);
  const [wind, setWind] = useState(null);
  const [airQuality, setAirQuality] = useState(null);
  const [description, setDescription] = useState('');

  const handleTagPress = (type, index) => {
    const tagCode = index + 1;
    switch (type) {
      case 'temperature':
        setTemperature(tagCode);
        break;
      case 'weather':
        setWeather(tagCode);
        break;
      case 'humidity':
        setHumidity(tagCode);
        break;
      case 'wind':
        setWind(tagCode);
        break;
      case 'airQuality':
        setAirQuality(tagCode);
        break;
      default:
        break;
    }
  };

  const handleSubmit = async () => {
    const postData = {
      content: description,
      temperatureTagCode: temperature,
      skyTagCode: weather,
      humidityTagCode: humidity,
      windTagCode: wind,
      dustTagCode: airQuality,
    };

    try {
      const response = await createPost(postData, accessToken, memberId);
      console.log('Post created successfully:', response);
      navigation.navigate('Home');
    } catch (error) {
      console.error('Failed to create post:', error.message);
      console.error('Error details:', error);
    }
  };

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.contentContainer}>
      <TextInput
        style={styles.textInput}
        placeholder="현재 날씨가 어떤지, 오늘 입은 옷 등을 공유해 주세요"
        placeholderTextColor="#888"
        multiline
        value={description}
        onChangeText={setDescription}
      />
      <View style={styles.section}>
        <Text style={styles.label}>온도는 어떤가요?</Text>
        <View style={styles.tagContainer}>
          {[
            '추움',
            '추움?',
            '조금 추움',
            '선선',
            '보통',
            '따뜻',
            '조금 따뜻',
            '조금 더움',
            '더움',
            '매우 더움',
          ].map((item, index) => (
            <TouchableOpacity
              key={item}
              style={[
                styles.tag,
                temperature === index + 1 && styles.selectedTag,
              ]}
              onPress={() => handleTagPress('temperature', index)}>
              <Text
                style={[
                  styles.tagText,
                  temperature === index + 1 && styles.selectedTagText,
                ]}>
                {item}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>날씨는 어떤가요?</Text>
        <View style={styles.tagContainer}>
          {['비와요', '흐려요', '맑고 구름이 많아요', '맑아요', '화창해요'].map(
            (item, index) => (
              <TouchableOpacity
                key={item}
                style={[
                  styles.tag,
                  weather === index + 1 && styles.selectedTag,
                ]}
                onPress={() => handleTagPress('weather', index)}>
                <Text
                  style={[
                    styles.tagText,
                    weather === index + 1 && styles.selectedTagText,
                  ]}>
                  {item}
                </Text>
              </TouchableOpacity>
            ),
          )}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>습한가요?</Text>
        <View style={styles.tagContainer}>
          {['건조함', '보통', '약간 습함', '습함', '매우 습함'].map(
            (item, index) => (
              <TouchableOpacity
                key={item}
                style={[
                  styles.tag,
                  humidity === index + 1 && styles.selectedTag,
                ]}
                onPress={() => handleTagPress('humidity', index)}>
                <Text
                  style={[
                    styles.tagText,
                    humidity === index + 1 && styles.selectedTagText,
                  ]}>
                  {item}
                </Text>
              </TouchableOpacity>
            ),
          )}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>바람은 어떤가요?</Text>
        <View style={styles.tagContainer}>
          {['안 불어요', '조금 불어요', '많이 불어요'].map((item, index) => (
            <TouchableOpacity
              key={item}
              style={[styles.tag, wind === index + 1 && styles.selectedTag]}
              onPress={() => handleTagPress('wind', index)}>
              <Text
                style={[
                  styles.tagText,
                  wind === index + 1 && styles.selectedTagText,
                ]}>
                {item}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>미세먼지는 어떤가요?</Text>
        <View style={styles.tagContainer}>
          {['매우 좋음', '좋음', '보통', '약간 나쁨', '매우 나쁨'].map(
            (item, index) => (
              <TouchableOpacity
                key={item}
                style={[
                  styles.tag,
                  airQuality === index + 1 && styles.selectedTag,
                ]}
                onPress={() => handleTagPress('airQuality', index)}>
                <Text
                  style={[
                    styles.tagText,
                    airQuality === index + 1 && styles.selectedTagText,
                  ]}>
                  {item}
                </Text>
              </TouchableOpacity>
            ),
          )}
        </View>
      </View>
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
    paddingTop: 50,
  },
  textInput: {
    height: 100,
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 8,
    paddingHorizontal: 10,
    marginBottom: 20,
    textAlignVertical: 'top',
    color: '#000',
  },
  section: {
    marginBottom: 22,
  },
  label: {
    fontSize: 16,
    marginBottom: 8,
    color: '#333',
  },
  tagContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  tag: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 20,
    backgroundColor: '#f0f0f0',
    marginRight: 8,
    marginBottom: 8,
  },
  tagText: {
    fontSize: 14,
    color: '#333',
  },
  selectedTag: {
    backgroundColor: '#2f5af4',
  },
  selectedTagText: {
    color: '#fff',
  },
  shareButton: {
    paddingVertical: 12,
    backgroundColor: '#2f5af4',
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 10,
  },
  shareButtonText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default PostCreationScreen;
