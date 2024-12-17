import React, {useState, useEffect, useCallback} from 'react';
import {
  View,
  StatusBar,
  StyleSheet,
  ScrollView,
  RefreshControl,
} from 'react-native';
import WeatherHeaderCommunity from '../components/WeatherHeaderCommunity';
import PostScroll from '../components/PostScroll';
import FloatingActionButton from '../components/FloatingActionButton';
import {useFocusEffect} from '@react-navigation/native';

const CommunityScreen = ({accessToken, navigation}) => {
  const [refreshPosts, setRefreshPosts] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const loadPosts = async (showRefreshIndicator = false) => {
    if (showRefreshIndicator) {
      setRefreshing(true);
    }
    setRefreshPosts(true);
    await new Promise(resolve => setTimeout(resolve, 1000));
    setRefreshPosts(false);
    setRefreshing(false);
  };

  useFocusEffect(
    useCallback(() => {
      loadPosts(false);
    }, []),
  );

  return (
    <View style={styles.container}>
      <StatusBar hidden={true} />
      <WeatherHeaderCommunity accessToken={accessToken} />

      <ScrollView
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={() => loadPosts(true)}
          />
        }>
        <PostScroll
          accessToken={accessToken}
          refreshPosts={refreshPosts}
          onRefreshComplete={() => setRefreshPosts(false)}
        />
      </ScrollView>

      <FloatingActionButton
        onPress={() => navigation.navigate('PostCreationScreen')}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F6FA',
  },
});

export default CommunityScreen;
