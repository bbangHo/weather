import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Dimensions,
} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';

const WeatherShareButton = () => {
  const navigation = useNavigation();
  const screenWidth = Dimensions.get('window').width;

  return (
    <View style={styles.container}>
      <Card containerStyle={[styles.card, globalStyles.transparentBackground]}>
        <Text style={styles.sectionText}>외출하셨나요?</Text>
        <Text style={styles.sectionText}>우리 동네 날씨를 알려주세요!</Text>
        <View style={styles.buttonContainer}>
          <TouchableOpacity
            style={styles.button}
            onPress={() => navigation.navigate('PostCreationScreen')}>
            <Text style={styles.buttonText}>날씨 공유하기</Text>
          </TouchableOpacity>
        </View>
      </Card>
    </View>
  );
};

const {width} = Dimensions.get('window');

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    marginRight: -10,
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 13,
    justifyContent: 'space-between',
    width: width / 2.1,
    height: Platform.OS === 'ios' ? 107 : 115,
  },
  sectionText: {
    color: '#fff',
    fontSize: 12,
    textAlign: 'center',
    marginBottom: 5,
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: 10,
  },
  button: {
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    padding: 10,
    paddingTop: 5,
    borderRadius: 5,
    alignItems: 'center',
    width: '100%',
  },
  buttonText: {
    color: '#fff',
    fontSize: 13,
  },
});

export default WeatherShareButton;
