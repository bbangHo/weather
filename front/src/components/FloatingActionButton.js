import React from 'react';
import {TouchableOpacity, Image, StyleSheet, Dimensions} from 'react-native';

const {width, height} = Dimensions.get('window');

const FloatingActionButton = ({onPress}) => {
  return (
    <TouchableOpacity style={styles.button} onPress={onPress}>
      <Image
        source={require('../../assets/images/icon_button.png')}
        style={styles.icon}
      />
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  button: {
    position: 'absolute',
    bottom: height * 0.03,
    right: width * 0.05,
    backgroundColor: '#3f7dfd',
    width: 60,
    height: 60,
    borderRadius: 30,
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 5,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.3,
    shadowRadius: 4,
  },
  icon: {
    width: 30,
    height: 30,
    tintColor: '#fff',
  },
});

export default FloatingActionButton;
