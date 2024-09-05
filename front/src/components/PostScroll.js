import React, {useEffect, useState} from 'react';
import {
  FlatList,
  Text,
  View,
  StyleSheet,
  Image,
  Alert,
  TouchableOpacity,
} from 'react-native';
import {Card} from 'react-native-elements';
import Icon from 'react-native-vector-icons/Ionicons';
import globalStyles from '../globalStyles';
import {fetchPosts, toggleLikePost} from '../api/api';

const PostScroll = ({accessToken, memberId}) => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadPosts();
  }, []);

  const loadPosts = async () => {
    try {
      const fetchedPosts = await fetchPosts(accessToken, memberId);
      console.log('Fetched posts:', fetchedPosts);
      if (fetchedPosts && fetchedPosts.length > 0) {
        setPosts(fetchedPosts);
      } else {
        Alert.alert('Info', '불러올 게시글이 없습니다.');
      }
    } catch (error) {
      console.error('Error loading posts:', error.message);
      Alert.alert(
        'Error',
        '서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.',
      );
    } finally {
      setLoading(false);
    }
  };

  const handleLikePress = async postId => {
    console.log('Like button pressed for postId:', postId);
    try {
      const response = await toggleLikePost(accessToken, memberId, postId);
      console.log('Like post response:', response);
      if (response.isSuccess) {
        setPosts(prevPosts =>
          prevPosts.map(post =>
            post.postInfo.postId === postId
              ? {
                  ...post,
                  postInfo: {
                    ...post.postInfo,
                    likeClickable: false,
                    likeCount: post.postInfo.likeCount + 1,
                  },
                }
              : post,
          ),
        );
      } else {
        Alert.alert('Error', '좋아요를 할 수 없습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('Failed to like post:', error.message);
      Alert.alert(
        'Error',
        '서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.',
      );
    }
  };

  const renderPost = ({item}) => (
    <View style={styles.section}>
      <Card containerStyle={[styles.card, globalStyles.transparentBackground]}>
        <View style={styles.header}>
          <View style={styles.profileInfo}>
            <Image
              source={
                item.memberInfo.profileImageUrl
                  ? {uri: item.memberInfo.profileImageUrl}
                  : require('../../assets/images/profile.png')
              }
              style={styles.profileImage}
              onError={() => {}}
            />
            <View style={styles.userInfo}>
              <View style={styles.userRow}>
                <Text style={styles.username}>
                  {item.memberInfo.memberName}
                </Text>
                <Icon
                  name="sunny-outline"
                  size={18}
                  color="#fff"
                  style={styles.userIcon}
                />
              </View>
              <Text style={styles.timeAgo}>{item.postInfo.createdAt}</Text>
            </View>
          </View>
          <TouchableOpacity
            style={styles.likeContainer}
            onPress={() => handleLikePress(item.postInfo.postId)}>
            <Icon
              name={item.postInfo.likeClickable ? 'heart-outline' : 'heart'}
              size={20}
              color="#fff"
              style={styles.likeIcon}
            />
            <Text style={styles.likeCount}>{item.postInfo.likeCount}</Text>
          </TouchableOpacity>
        </View>
        <Text style={styles.content}>{item.postInfo.content}</Text>
      </Card>
    </View>
  );

  return (
    <FlatList
      data={posts}
      renderItem={renderPost}
      keyExtractor={item => item.postInfo.postId.toString()}
      ListFooterComponent={
        loading ? <Text style={styles.loadingText}>Loading...</Text> : null
      }
      contentContainerStyle={styles.contentContainer}
    />
  );
};

const styles = StyleSheet.create({
  contentContainer: {
    alignItems: 'center',
  },
  section: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 15,
    marginTop: 0,
    marginBottom: 10,
    width: 350,
    height: 130,
    justifyContent: 'space-between',
    position: 'relative',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  profileInfo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  profileImage: {
    width: 35,
    height: 35,
    borderRadius: 20,
    marginRight: 10,
  },
  userInfo: {
    flex: 1,
  },
  userRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  username: {
    color: '#fff',
    fontWeight: 'bold',
  },
  userIcon: {
    marginLeft: 7,
  },
  timeAgo: {
    color: '#ddd',
    fontSize: 12,
    marginTop: 2,
  },
  likeContainer: {
    position: 'absolute',
    top: -3,
    right: 0,
    alignItems: 'center',
  },
  likeIcon: {
    marginBottom: 1,
  },
  likeCount: {
    color: '#fff',
    fontSize: 13,
  },
  content: {
    color: '#fff',
    marginTop: 13,
  },
  loadingText: {
    textAlign: 'center',
    padding: 10,
    color: '#fff',
  },
});

export default PostScroll;
