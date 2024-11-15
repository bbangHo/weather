import React, {useEffect, useState} from 'react';
import {
  FlatList,
  Text,
  View,
  StyleSheet,
  Image,
  Alert,
  TouchableOpacity,
  Dimensions,
} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import {fetchPosts, toggleLikePost} from '../api/api';

const {width: windowWidth} = Dimensions.get('window');

const PostScroll = ({accessToken, refreshPosts, onRefreshComplete}) => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const postType = 'WEATHER';

  useEffect(() => {
    loadPosts();
  }, []);

  useEffect(() => {
    if (refreshPosts) {
      loadPosts();
      if (onRefreshComplete) {
        onRefreshComplete();
      }
    }
  }, [refreshPosts]);

  const loadPosts = async () => {
    try {
      const fetchedPosts = await fetchPosts(
        accessToken,
        postType,
        null,
        null,
        100000000,
      );
      console.log('Fetched posts:', fetchedPosts);

      setPosts(
        fetchedPosts.map(post => ({
          ...post,
          postInfo: {
            ...post.postInfo,
            likeClickable: post.postInfo.likeCount > 0,
          },
        })),
      );
    } catch (error) {
      console.error('Error loading posts:', error.message);
      Alert.alert(
        'Error',
        '게시글을 불러오는 중 문제가 발생했습니다. 다시 시도해주세요.',
      );
    } finally {
      setLoading(false);
    }
  };

  const handleLikePress = async postId => {
    try {
      const response = await toggleLikePost(accessToken, postId);
      if (response.isSuccess) {
        setPosts(prevPosts =>
          prevPosts.map(post =>
            post.postInfo.postId === postId
              ? {
                  ...post,
                  postInfo: {
                    ...post.postInfo,
                    likeClickable: !post.postInfo.likeClickable,
                    likeCount: post.postInfo.likeClickable
                      ? Math.max(post.postInfo.likeCount - 1, 0)
                      : post.postInfo.likeCount + 1,
                  },
                }
              : post,
          ),
        );
      } else {
        Alert.alert('Error', '좋아요를 할 수 없습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('Failed to like/unlike post:', error.message);
      Alert.alert(
        'Error',
        '서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.',
      );
    }
  };

  const getUserIcon = sensitivity => {
    switch (sensitivity) {
      case 'HOT':
        return require('../../assets/images/icon_clear.png');
      case 'NONE':
        return require('../../assets/images/icon_partlycloudy.png');
      case 'COLD':
        return require('../../assets/images/icon_snow2.png');
      default:
        return null;
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
                <Image
                  source={getUserIcon(item.memberInfo.sensitivity)}
                  style={styles.userIcon}
                />
              </View>
              <Text style={styles.timeAgo}>{item.postInfo.createdAt}</Text>
            </View>
          </View>
          <TouchableOpacity
            style={styles.likeContainer}
            onPress={() => handleLikePress(item.postInfo.postId)}>
            <Image
              source={
                item.postInfo.likeClickable
                  ? require('../../assets/images/icon_heart2.png')
                  : require('../../assets/images/icon_heart0.png')
              }
              style={[
                styles.likeIcon,
                {
                  tintColor: item.postInfo.likeClickable
                    ? '#da4133'
                    : '#d3d3d3',
                },
              ]}
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
      contentContainerStyle={styles.contentContainer}
      ListEmptyComponent={
        loading ? (
          <Text style={styles.loadingText}>Loading...</Text>
        ) : (
          <Text style={styles.noPostText}>불러올 게시글이 없습니다.</Text>
        )
      }
    />
  );
};

const styles = StyleSheet.create({
  contentContainer: {
    alignItems: 'center',
    marginBottom: '100',
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
    width: windowWidth * 0.93,
    height: Platform.OS === 'ios' ? 130 : 155,
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
    width: 18,
    height: 18,
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
    width: 20,
    height: 20,
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
  noPostText: {
    color: '#fff',
    textAlign: 'center',
    marginTop: 20,
  },
});

export default PostScroll;
