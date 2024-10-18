import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  ScrollView,
} from 'react-native';

const InterestPostCreationScreen = ({navigation}) => {
  const [description, setDescription] = useState('');

  const handleSubmit = () => {
    console.log('Post content:', description);
    navigation.navigate('InterestScreen');
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

export default InterestPostCreationScreen;
