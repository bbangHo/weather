import React from 'react';
import {ScrollView, Text, View, StyleSheet} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';

const Posts = () => {
  return (
    <ScrollView horizontal style={styles.container}>
      {[...Array(5)].map((_, i) => (
        <Card
          key={i}
          containerStyle={[styles.card, globalStyles.transparentBackground]}>
          <Text style={styles.text}>작성자 이름</Text>
          <Text style={styles.text}>글자 수 제한은 어떻게?</Text>
        </Card>
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    marginVertical: 5,
    paddingHorizontal: 5,
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 15,
    marginHorizontal: 5,
    width: 350,
    height: 100,
  },
  text: {
    color: '#fff',
  },
});

export default Posts;
