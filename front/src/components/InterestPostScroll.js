import React, {useEffect, useState} from 'react';
import {
  FlatList,
  Text,
  View,
  StyleSheet,
  Image,
  TouchableOpacity,
  Modal,
  Dimensions,
  Alert,
} from 'react-native';
import {Card} from 'react-native-elements';
import globalStyles from '../globalStyles';
import {fetchPosts, toggleLikePost} from '../api/api';

const {width} = Dimensions.get('window');

const hobbies = [
  {
    id: 1,
    name: '런닝',
    postType: 'RUN',
    icon: require('../../assets/images/icon_interest_run.png'),
  },
  {
    id: 2,
    name: '등산',
    postType: 'HIKING',
    icon: require('../../assets/images/icon_interest_hiking.png'),
  },
  {
    id: 3,
    name: '반려동물 산책',
    postType: 'PET',
    icon: require('../../assets/images/icon_interest_pet.png'),
  },
];

const InterestPostScroll = ({accessToken, selectedHobby}) => {
  const [posts, setPosts] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [currentHobby, setCurrentHobby] = useState(selectedHobby);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (currentHobby) {
      loadPosts();
    }
  }, [currentHobby]);

  const loadPosts = async () => {
    try {
      setLoading(true);
      const fetchedPosts = await fetchPosts(accessToken, currentHobby.postType);
      console.log(
        'Fetched interest posts:',
        JSON.stringify(fetchedPosts, null, 2),
      );

      const uniquePosts = Array.from(
        new Set(fetchedPosts.map(post => post.postInfo.postId)),
      ).map(id => fetchedPosts.find(post => post.postInfo.postId === id));
      setPosts(uniquePosts);
    } catch (error) {
      console.error('Error fetching posts:', error);
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
                      ? post.postInfo.likeCount + 1
                      : post.postInfo.likeCount - 1,
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

  const handleHobbySelect = hobby => {
    setCurrentHobby(hobby);
    setModalVisible(false);
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
            />
            <View style={styles.userInfo}>
              <View style={styles.userRow}>
                <Text style={styles.username}>
                  {item.memberInfo.memberName}
                </Text>
                {item.memberInfo.sensitivity && (
                  <Image
                    source={getUserIcon(item.memberInfo.sensitivity)}
                    style={styles.userIcon}
                  />
                )}
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
                  ? require('../../assets/images/icon_heart0.png')
                  : require('../../assets/images/icon_heart2.png')
              }
              style={[
                styles.likeIcon,
                {
                  tintColor: item.postInfo.likeClickable
                    ? '#3f51b5'
                    : '#da4133',
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
    <View style={{flex: 1}}>
      <View style={styles.topBar}>
        <TouchableOpacity
          style={styles.hobbySelector}
          onPress={() => setModalVisible(true)}>
          <Text style={styles.hobbySelectorText}>
            {currentHobby ? currentHobby.name : '취미 선택 ▼'}
          </Text>
        </TouchableOpacity>
      </View>

      {loading ? (
        <Text style={styles.noPostText}>
          아직 작성된 게시글이 없습니다.{'\n'}첫 번째 글을 작성해 주세요!
        </Text>
      ) : (
        <FlatList
          data={posts}
          renderItem={renderPost}
          keyExtractor={item => item.postInfo.postId.toString()}
          ListEmptyComponent={
            <Text style={styles.noPostText}>
              아직 작성된 게시글이 없습니다.{'\n'}첫 번째 글을 작성해 주세요!
            </Text>
          }
          contentContainerStyle={styles.contentContainer}
        />
      )}

      <Modal
        visible={modalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setModalVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContainer}>
            <Text style={styles.modalTitle}>취미 선택</Text>
            <FlatList
              data={hobbies}
              numColumns={2}
              renderItem={({item}) => (
                <TouchableOpacity
                  style={[
                    styles.hobbyButton,
                    currentHobby?.id === item.id && styles.selectedHobby,
                  ]}
                  onPress={() => handleHobbySelect(item)}>
                  <Image
                    source={item.icon}
                    style={[
                      styles.hobbyIcon,
                      {
                        tintColor:
                          currentHobby?.id === item.id ? '#fff' : '#3f51b5',
                      },
                    ]}
                  />
                  <Text
                    style={[
                      styles.hobbyButtonText,
                      currentHobby?.id === item.id && styles.selectedHobbyText,
                    ]}>
                    {item.name}
                  </Text>
                </TouchableOpacity>
              )}
              keyExtractor={item => item.id.toString()}
              contentContainerStyle={styles.hobbyList}
            />
            <TouchableOpacity
              style={styles.applyButton}
              onPress={() => setModalVisible(false)}>
              <Text style={styles.applyText}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </View>
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
    marginTop: Platform.OS === 'ios' ? 2 : 8,
    marginBottom: Platform.OS === 'ios' ? 8 : 2,
    width: width * 0.93,
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
  noPostText: {
    color: '#fff',
    textAlign: 'center',
    marginTop: 20,
    fontSize: 15,
  },
  topBar: {
    position: 'absolute',
    top: 0,
    right: 0,
    marginRight: 15,
    marginTop: 10,
    zIndex: 1,
  },
  hobbySelector: {
    marginTop: -60,
    paddingVertical: 10,
    paddingBottom: 13,
    paddingHorizontal: 20,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 10,
  },
  hobbySelectorText: {
    color: '#fff',
    fontSize: 16,
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContainer: {
    backgroundColor: '#fff',
    width: width * 1,
    borderRadius: 10,
    padding: 20,
    alignItems: 'center',
  },
  modalTitle: {
    fontSize: 20,
    marginBottom: 20,
    color: '#3f51b5',
  },
  hobbyButton: {
    paddingVertical: 20,
    margin: 10,
    borderWidth: 1,
    borderColor: '#3f51b5',
    borderRadius: 10,
    alignItems: 'center',
    justifyContent: 'center',
    width: width * 0.4,
  },
  hobbyButtonText: {
    fontSize: 14,
    color: '#3f51b5',
    marginTop: 5,
    textAlign: 'center',
  },
  selectedHobby: {
    backgroundColor: '#3f51b5',
  },
  selectedHobbyText: {
    color: '#fff',
  },
  hobbyIcon: {
    width: 30,
    height: 30,
    marginBottom: 5,
  },
  applyButton: {
    backgroundColor: '#3f51b5',
    paddingVertical: 10,
    paddingHorizontal: 40,
    borderRadius: 5,
    marginTop: 20,
  },
  applyText: {
    color: '#fff',
    fontSize: 16,
  },
});

export default InterestPostScroll;
