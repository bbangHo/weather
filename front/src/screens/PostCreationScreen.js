import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  ScrollView,
} from 'react-native';
import {fetchPostTags, createPost} from '../api/api';

const PostCreationScreen = ({navigation, accessToken, memberId}) => {
  const [temperatureTags, setTemperatureTags] = useState([]);
  const [weatherTags, setWeatherTags] = useState([]);
  const [humidityTags, setHumidityTags] = useState([]);
  const [windTags, setWindTags] = useState([]);
  const [airQualityTags, setAirQualityTags] = useState([]);

  const [temperature, setTemperature] = useState(null);
  const [weather, setWeather] = useState(null);
  const [humidity, setHumidity] = useState(null);
  const [wind, setWind] = useState(null);
  const [airQuality, setAirQuality] = useState(null);
  const [description, setDescription] = useState('');

  useEffect(() => {
    const loadTags = async () => {
      try {
        const tags = await fetchPostTags(accessToken);
        setTemperatureTags(tags.TemperatureTag);
        setWeatherTags(tags.SkyTag);
        setHumidityTags(tags.HumidityTag);
        setWindTags(tags.WindTag);
        setAirQualityTags(tags.DustTag);
      } catch (error) {
        console.error('Failed to fetch tags:', error);
      }
    };
    loadTags();
  }, [accessToken]);

  const handleTagPress = (type, selectedTag) => {
    switch (type) {
      case 'temperature':
        setTemperature(selectedTag.code);
        break;
      case 'weather':
        setWeather(selectedTag.code);
        break;
      case 'humidity':
        setHumidity(selectedTag.code);
        break;
      case 'wind':
        setWind(selectedTag.code);
        break;
      case 'airQuality':
        setAirQuality(selectedTag.code);
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
      console.log('Post data to send:', postData);
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
          {temperatureTags.map(tag => (
            <TouchableOpacity
              key={tag.code}
              style={[
                styles.tag,
                temperature === tag.code && styles.selectedTag,
              ]}
              onPress={() => handleTagPress('temperature', tag)}>
              <Text
                style={[
                  styles.tagText,
                  temperature === tag.code && styles.selectedTagText,
                ]}>
                {tag.text}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>날씨는 어떤가요?</Text>
        <View style={styles.tagContainer}>
          {weatherTags.map(tag => (
            <TouchableOpacity
              key={tag.code}
              style={[styles.tag, weather === tag.code && styles.selectedTag]}
              onPress={() => handleTagPress('weather', tag)}>
              <Text
                style={[
                  styles.tagText,
                  weather === tag.code && styles.selectedTagText,
                ]}>
                {tag.text}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>습한가요?</Text>
        <View style={styles.tagContainer}>
          {humidityTags.map(tag => (
            <TouchableOpacity
              key={tag.code}
              style={[styles.tag, humidity === tag.code && styles.selectedTag]}
              onPress={() => handleTagPress('humidity', tag)}>
              <Text
                style={[
                  styles.tagText,
                  humidity === tag.code && styles.selectedTagText,
                ]}>
                {tag.text}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>바람은 어떤가요?</Text>
        <View style={styles.tagContainer}>
          {windTags.map(tag => (
            <TouchableOpacity
              key={tag.code}
              style={[styles.tag, wind === tag.code && styles.selectedTag]}
              onPress={() => handleTagPress('wind', tag)}>
              <Text
                style={[
                  styles.tagText,
                  wind === tag.code && styles.selectedTagText,
                ]}>
                {tag.text}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
      <View style={styles.section}>
        <Text style={styles.label}>미세먼지는 어떤가요?</Text>
        <View style={styles.tagContainer}>
          {airQualityTags.map(tag => (
            <TouchableOpacity
              key={tag.code}
              style={[
                styles.tag,
                airQuality === tag.code && styles.selectedTag,
              ]}
              onPress={() => handleTagPress('airQuality', tag)}>
              <Text
                style={[
                  styles.tagText,
                  airQuality === tag.code && styles.selectedTagText,
                ]}>
                {tag.text}
              </Text>
            </TouchableOpacity>
          ))}
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
