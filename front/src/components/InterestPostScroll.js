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
} from 'react-native';
import {Card} from 'react-native-elements';
import Icon from 'react-native-vector-icons/Ionicons';
import globalStyles from '../globalStyles';

const {width} = Dimensions.get('window');

const dummyPosts = [
  {
    postInfo: {
      postId: 1,
      content: '오늘은 런닝하기 좋은 날입니다!',
      createdAt: '1시간 전',
      likeCount: 34,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저1',
      profileImageUrl: null,
    },
    hobby: '런닝',
  },
  {
    postInfo: {
      postId: 2,
      content: '등산하기에 최적의 날씨네요!',
      createdAt: '2시간 전',
      likeCount: 12,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저2',
      profileImageUrl: null,
    },
    hobby: '등산',
  },
  {
    postInfo: {
      postId: 3,
      content: '반려동물 산책을 즐기기에 좋은 날입니다!',
      createdAt: '3시간 전',
      likeCount: 22,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저3',
      profileImageUrl: null,
    },
    hobby: '반려동물 산책',
  },
  {
    postInfo: {
      postId: 4,
      content: '반려동물 산책을 즐기기에 좋은 날입니다!',
      createdAt: '3시간 전',
      likeCount: 22,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저3',
      profileImageUrl: null,
    },
    hobby: '반려동물 산책',
  },
  {
    postInfo: {
      postId: 5,
      content: '반려동물 산책을 즐기기에 좋은 날입니다!',
      createdAt: '3시간 전',
      likeCount: 22,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저3',
      profileImageUrl: null,
    },
    hobby: '반려동물 산책',
  },
  {
    postInfo: {
      postId: 6,
      content: '반려동물 산책을 즐기기에 좋은 날입니다!',
      createdAt: '3시간 전',
      likeCount: 22,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저3',
      profileImageUrl: null,
    },
    hobby: '반려동물 산책',
  },
  {
    postInfo: {
      postId: 7,
      content: '반려동물 산책을 즐기기에 좋은 날입니다!',
      createdAt: '3시간 전',
      likeCount: 22,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저3',
      profileImageUrl: null,
    },
    hobby: '반려동물 산책',
  },
  {
    postInfo: {
      postId: 8,
      content: '반려동물 산책을 즐기기에 좋은 날입니다!',
      createdAt: '3시간 전',
      likeCount: 22,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저3',
      profileImageUrl: null,
    },
    hobby: '반려동물 산책',
  },
  {
    postInfo: {
      postId: 9,
      content: '반려동물 산책을 즐기기에 좋은 날입니다!',
      createdAt: '3시간 전',
      likeCount: 22,
      likeClickable: true,
    },
    memberInfo: {
      memberName: '유저3',
      profileImageUrl: null,
    },
    hobby: '반려동물 산책',
  },
];

const hobbies = [
  {id: 1, name: '런닝'},
  {id: 2, name: '등산'},
  {id: 3, name: '반려동물 산책'},
];

const InterestPostScroll = ({selectedHobby}) => {
  const [posts, setPosts] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [currentHobby, setCurrentHobby] = useState(selectedHobby);

  useEffect(() => {
    loadPosts();
  }, [currentHobby]);

  const loadPosts = () => {
    if (!currentHobby) {
      setPosts(dummyPosts);
    } else {
      const filteredPosts = dummyPosts.filter(
        post => post.hobby === currentHobby.name,
      );
      setPosts(filteredPosts);
    }
  };

  const handleLikePress = postId => {
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
  };

  const handleHobbySelect = hobby => {
    setCurrentHobby(hobby);
    setModalVisible(false);
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
                  <Icon name="paw-outline" size={30} color="#3f51b5" />
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
