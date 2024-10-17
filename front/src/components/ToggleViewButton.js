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
    justifyContent: 'center',
    marginVertical: 5,
    paddingHorizontal: 10,
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 0,
    paddingHorizontal: 10,
    minWidth: 80,
  },
  button: {
    backgroundColor: 'transparent',
    paddingHorizontal: 10,
    minWidth: 120,
  },
  buttonTitle: {
    fontSize: 14,
  },
});

export default ToggleViewButton;
