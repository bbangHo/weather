import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
} from 'react-native';

const PostCreationScreen = ({navigation}) => {
  const [temperature, setTemperature] = useState(null);
  const [weather, setWeather] = useState(null);
  const [humidity, setHumidity] = useState(null);
  const [wind, setWind] = useState(null);
  const [airQuality, setAirQuality] = useState(null);
  const [description, setDescription] = useState('');

  const handleTagPress = (type, value) => {
    switch (type) {
      case 'temperature':
        setTemperature(value);
        break;
      case 'weather':
        setWeather(value);
        break;
      case 'humidity':
        setHumidity(value);
        break;
      case 'wind':
        setWind(value);
        break;
      case 'airQuality':
        setAirQuality(value);
        break;
      default:
        break;
    }
  };

  return (
    <View style={styles.container}>
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
          {['더움', '조금 더움'].map(item => (
            <TouchableOpacity
              key={item}
              style={[styles.tag, temperature === item && styles.selectedTag]}
              onPress={() => handleTagPress('temperature', item)}>
              <Text
                style={[
                  styles.tagText,
                  temperature === item && styles.selectedTagText,
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
          {['비와요', '흐려요', '구름이 많아요', '화창해요'].map(item => (
            <TouchableOpacity
              key={item}
              style={[styles.tag, weather === item && styles.selectedTag]}
              onPress={() => handleTagPress('weather', item)}>
              <Text
                style={[
                  styles.tagText,
                  weather === item && styles.selectedTagText,
                ]}>
                {item}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>습한가요?</Text>
        <View style={styles.tagContainer}>
          {['습함', '조금 습함', '꽉꽉함', '습하지 않음'].map(item => (
            <TouchableOpacity
              key={item}
              style={[styles.tag, humidity === item && styles.selectedTag]}
              onPress={() => handleTagPress('humidity', item)}>
              <Text
                style={[
                  styles.tagText,
                  humidity === item && styles.selectedTagText,
                ]}>
                {item}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>바람은 어떤가요?</Text>
        <View style={styles.tagContainer}>
          {['많이 불어요', '조금 불어요', '안불어요'].map(item => (
            <TouchableOpacity
              key={item}
              style={[styles.tag, wind === item && styles.selectedTag]}
              onPress={() => handleTagPress('wind', item)}>
              <Text
                style={[
                  styles.tagText,
                  wind === item && styles.selectedTagText,
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
          {['안좋아요', '많이 안좋아요', '좋아요'].map(item => (
            <TouchableOpacity
              key={item}
              style={[styles.tag, airQuality === item && styles.selectedTag]}
              onPress={() => handleTagPress('airQuality', item)}>
              <Text
                style={[
                  styles.tagText,
                  airQuality === item && styles.selectedTagText,
                ]}>
                {item}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <TouchableOpacity style={styles.shareButton} onPress={() => {}}>
        <Text style={styles.shareButtonText}>공유하기</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#fff',
    paddingTop: 60,
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
