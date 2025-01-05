import React, {useState} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createStackNavigator} from '@react-navigation/stack';
import HomeScreen from './src/screens/HomeScreen';
import CommunityScreen from './src/screens/CommunityScreen';
import MyScreen from './src/screens/MyScreen';
import RegisterProfileScreen from './src/screens/RegisterProfileScreen';
import PostCreationScreen from './src/screens/PostCreationScreen';
import LoginScreen from './src/screens/LoginScreen';
import ProfileScreen from './src/screens/ProfileScreen';
import {StatusBar, Image, Platform} from 'react-native';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const AuthStack = ({setIsLoggedIn, setAccessToken, setIsNewMember}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="LoginScreen">
      {props => (
        <LoginScreen
          {...props}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
          setIsNewMember={setIsNewMember}
        />
      )}
    </Stack.Screen>
    <Stack.Screen
      name="RegisterProfileScreen"
      component={RegisterProfileScreen}
    />
  </Stack.Navigator>
);

const HomeStack = ({accessToken}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="Home">
      {props => <HomeScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
    <Stack.Screen name="PostCreationScreen">
      {props => <PostCreationScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
  </Stack.Navigator>
);

const MyStack = ({
  accessToken,
  setIsNewMember,
  setLocationId,
  setIsLoggedIn,
  setAccessToken,
}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="MyScreen">
      {props => (
        <MyScreen
          {...props}
          accessToken={accessToken}
          setIsNewMember={setIsNewMember}
          setLocationId={setLocationId}
          setIsLoggedIn={setIsLoggedIn}
          setAccessToken={setAccessToken}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="ProfileScreen">
      {props => <ProfileScreen {...props} accessToken={accessToken} />}
    </Stack.Screen>
  </Stack.Navigator>
);

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [accessToken, setAccessToken] = useState(null);
  const [isNewMember, setIsNewMember] = useState(false);
  const [locationId, setLocationId] = useState(null);

  return (
    <>
      <StatusBar
        translucent
        backgroundColor="transparent"
        barStyle="dark-content"
      />
      <NavigationContainer>
        {isLoggedIn ? (
          isNewMember ? (
            <Stack.Navigator screenOptions={{headerShown: false}}>
              <Stack.Screen name="RegisterProfileScreen">
                {props => (
                  <RegisterProfileScreen
                    {...props}
                    accessToken={accessToken}
                    setIsNewMember={setIsNewMember}
                  />
                )}
              </Stack.Screen>
            </Stack.Navigator>
          ) : (
            <Tab.Navigator
              screenOptions={({route}) => ({
                headerShown: false,
                tabBarIcon: ({focused, color}) => {
                  let iconSource;
                  let size;

                  switch (route.name) {
                    case 'HomeStack':
                      iconSource = require('./assets/images/icon_tab_home.png');
                      size = 26;
                      break;
                    case 'Community':
                      iconSource = require('./assets/images/icon_tab_community.png');
                      size = 24;
                      break;
                    case 'My':
                      iconSource = require('./assets/images/icon_tab_my.png');
                      size = 29;
                      break;
                    default:
                      size = 25;
                  }

                  return (
                    <Image
                      source={iconSource}
                      style={{
                        width: size,
                        height: size,
                        tintColor: focused ? '#3f51b5' : color,
                      }}
                    />
                  );
                },
                tabBarActiveTintColor: '#3f51b5',
                tabBarInactiveTintColor: 'gray',
                tabBarStyle: {
                  paddingTop: 5,
                  paddingBottom: 10,
                  height: 80,
                },
                tabBarLabelStyle: {
                  fontSize: Platform.OS === 'ios' ? 10 : 12,
                  paddingBottom: Platform.OS === 'ios' ? 18 : 10,
                },
              })}
              initialRouteName="HomeStack">
              <Tab.Screen
                name="HomeStack"
                options={{
                  tabBarLabel: '홈',
                }}>
                {props => <HomeStack {...props} accessToken={accessToken} />}
              </Tab.Screen>
              <Tab.Screen
                name="Community"
                options={{
                  tabBarLabel: '탐색',
                }}>
                {props => (
                  <CommunityScreen {...props} accessToken={accessToken} />
                )}
              </Tab.Screen>
              <Tab.Screen
                name="My"
                options={{
                  tabBarLabel: '프로필',
                }}>
                {props => (
                  <MyStack
                    {...props}
                    accessToken={accessToken}
                    setIsNewMember={setIsNewMember}
                    setLocationId={setLocationId}
                    setIsLoggedIn={setIsLoggedIn}
                    setAccessToken={setAccessToken}
                  />
                )}
              </Tab.Screen>
            </Tab.Navigator>
          )
        ) : (
          <AuthStack
            setIsLoggedIn={setIsLoggedIn}
            setAccessToken={setAccessToken}
            setIsNewMember={setIsNewMember}
          />
        )}
      </NavigationContainer>
    </>
  );
};

export default App;
