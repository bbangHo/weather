import React, {useEffect, useState} from 'react';
import {FlatList, Text, View, StyleSheet, Image} from 'react-native';
import {Card} from 'react-native-elements';
import Icon from 'react-native-vector-icons/Ionicons';
import globalStyles from '../globalStyles';

const PostScroll = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);

  useEffect(() => {
    loadPosts();
  }, [page]);

  const loadPosts = () => {
    if (loading) return;

    setLoading(true);
    const newPosts = Array.from({length: 10}).map((_, index) => ({
      id: `post-${page}-${index}-${Date.now()}`,
      username: '작성자 이름',
      timeAgo: `${Math.floor(Math.random() * 60)}분 전`,
      content: '글자 수 제한은 어떻게 하면 좋을지 고민',
      likes: Math.floor(Math.random() * 100),
    }));

    setPosts(prevPosts => [...prevPosts, ...newPosts]);
    setLoading(false);
  };

  const handleLoadMore = () => {
    if (!loading) {
      setPage(page + 1);
    }
  };

  const renderPost = ({item}) => (
    <View style={styles.section}>
      <Card containerStyle={[styles.card, globalStyles.transparentBackground]}>
        <View style={styles.header}>
          <View style={styles.profileInfo}>
            <Image
              source={require('../../assets/images/profile.png')}
              style={styles.profileImage}
            />
            <View style={styles.userInfo}>
              <View style={styles.userRow}>
                <Text style={styles.username}>{item.username}</Text>
                <Icon
                  name="sunny-outline"
                  size={18}
                  color="#fff"
                  style={styles.userIcon}
                />
              </View>
              <Text style={styles.timeAgo}>{item.timeAgo}</Text>
            </View>
          </View>
          <View style={styles.likeContainer}>
            <Icon
              name="heart-outline"
              size={20}
              color="#fff"
              style={styles.likeIcon}
            />
            <Text style={styles.likeCount}>{item.likes}</Text>
          </View>
        </View>
        <Text style={styles.content}>{item.content}</Text>
      </Card>
    </View>
  );

  return (
    <FlatList
      data={posts}
      renderItem={renderPost}
      keyExtractor={item => item.id}
      onEndReached={handleLoadMore}
      onEndReachedThreshold={0.5}
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
