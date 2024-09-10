import React from 'react';
import {View, StyleSheet} from 'react-native';
import {Button, Card} from 'react-native-elements';
import globalStyles from '../globalStyles';

const ToggleViewButton = ({showText, setShowText}) => {
  return (
    <View style={styles.container}>
      <Card containerStyle={[globalStyles.transparentBackground, styles.card]}>
        <Button
          title={showText ? '수치로 보기' : '텍스트로 보기'}
          onPress={() => setShowText(prev => !prev)}
          buttonStyle={styles.button}
          titleStyle={styles.buttonTitle}
        />
      </Card>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginVertical: 5,
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 0,
  },
  button: {
    backgroundColor: 'transparent',
  },
  buttonTitle: {
    fontSize: 13,
  },
});

export default ToggleViewButton;
