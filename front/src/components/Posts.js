import React, {useEffect, useState} from 'react';
import {
  FlatList,
  Text,
  View,
  StyleSheet,
  Image,
  Dimensions,
  TouchableOpacity,
  Alert,
} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import {fetchPopularPosts, toggleLikePost} from '../api/api';

const Posts = ({accessToken, memberId}) => {
  const screenWidth = Dimensions.get('window').width;
  const navigation = useNavigation();
  const [newPosts, setNewPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  const handleLikePress = async postId => {
    console.log('Like button pressed for postId:', postId);
    try {
      const response = await toggleLikePost(accessToken, memberId, postId);
      console.log('Like post response:', response);

      if (response.isSuccess) {
        setNewPosts(prevPosts =>
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

  useEffect(() => {
    const loadPosts = async () => {
      try {
        const posts = await fetchPopularPosts(accessToken, memberId);
        console.log('Fetched posts:', posts);
        setNewPosts(posts);
      } catch (error) {
        console.error('Error fetching popular posts:', error.message);
      } finally {
        setLoading(false);
      }
    };

    loadPosts();
  }, [accessToken, memberId]);

  const renderPost = ({item}) => (
    <View style={[styles.section, {width: screenWidth}]}>
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
            />
            <View style={styles.userInfo}>
              <View style={styles.userRow}>
                <Text style={styles.username}>
                  {item.memberInfo.memberName}
                </Text>
                <Image
                  source={require('../../assets/images/icon_clear.png')}
                  style={styles.userIcon}
                />
              </View>
              <Text style={styles.timeAgo}>{item.postInfo.createdAt}</Text>
            </View>
          </View>

          <TouchableOpacity
            style={styles.likeContainer}
            onPress={() => {
              console.log(
                'TouchableOpacity pressed, postId:',
                item.postInfo.postId,
              );
              handleLikePress(item.postInfo.postId);
            }}
            disabled={!item.postInfo.likeClickable}>
            <Image
              source={
                item.postInfo.likeClickable
                  ? require('../../assets/images/icon_nonheart.png')
                  : require('../../assets/images/icon_heart2.png')
              }
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
      data={newPosts}
      keyExtractor={item => item.postInfo.postId.toString()}
      renderItem={renderPost}
      horizontal
      pagingEnabled
      showsHorizontalScrollIndicator={false}
      ListHeaderComponent={
        <View style={[styles.section, {width: screenWidth}]}>
          <Card
            containerStyle={[styles.card, globalStyles.transparentBackground]}>
            <Text style={styles.sectionText}>외출하셨나요?</Text>
            <Text style={styles.sectionText}>우리 동네의 날씨는 어떤가요?</Text>
            <View style={styles.buttonContainer}>
              <TouchableOpacity
                style={styles.button}
                onPress={() => navigation.navigate('PostCreationScreen')}>
                <Text style={styles.buttonText}>날씨 공유하기</Text>
              </TouchableOpacity>
            </View>
          </Card>
        </View>
      }
      ListFooterComponent={
        <View style={[styles.section, {width: screenWidth}]}>
          <Card
            containerStyle={[styles.card, globalStyles.transparentBackground]}>
            <View style={styles.moreContainer}>
              <Text style={styles.moreText}>더 보기</Text>
            </View>
          </Card>
        </View>
      }
    />
  );
};

const styles = StyleSheet.create({
  section: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  card: {
    borderRadius: 10,
    borderColor: 'rgba(255, 255, 255, 0)',
    padding: 15,
    marginHorizontal: 5,
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
  },
  likeCount: {
    color: '#fff',
    fontSize: 13,
  },
  content: {
    color: '#fff',
    marginTop: 13,
  },
  sectionText: {
    color: '#fff',
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 5,
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 10,
  },
  button: {
    flex: 1,
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    shadowColor: 'transparent',
    elevation: 0,
    padding: 10,
    borderRadius: 5,
    marginHorizontal: 5,
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontSize: 14,
  },
  moreContainer: {
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    height: 110,
  },
  moreText: {
    color: '#fff',
    fontSize: 18,
    textAlign: 'center',
  },
});

export default Posts;
