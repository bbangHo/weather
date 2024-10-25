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
import InterestScreen from './src/screens/InterestScreen';
import InterestPostCreationScreen from './src/screens/InterestPostCreationScreen';
import {StatusBar, Image, Button} from 'react-native';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const HomeStack = ({accessToken, memberId}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="Home">
      {props => (
        <HomeScreen {...props} accessToken={accessToken} memberId={memberId} />
      )}
    </Stack.Screen>
    <Stack.Screen name="PostCreationScreen">
      {props => (
        <PostCreationScreen
          {...props}
          accessToken={accessToken}
          memberId={memberId}
        />
      )}
    </Stack.Screen>
  </Stack.Navigator>
);

const InterestStack = ({accessToken, memberId}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="InterestScreen">
      {props => (
        <InterestScreen
          {...props}
          accessToken={accessToken}
          memberId={memberId}
        />
      )}
    </Stack.Screen>
    <Stack.Screen name="InterestPostCreationScreen">
      {props => (
        <InterestPostCreationScreen
          {...props}
          accessToken={accessToken}
          memberId={memberId}
        />
      )}
    </Stack.Screen>
  </Stack.Navigator>
);

const RegisterProfileStack = ({accessToken, memberId, setIsNewMember}) => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="RegisterProfileScreen">
      {props => (
        <RegisterProfileScreen
          {...props}
          accessToken={accessToken}
          memberId={memberId}
          setIsNewMember={setIsNewMember}
        />
      )}
    </Stack.Screen>
  </Stack.Navigator>
);

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [accessToken, setAccessToken] = useState(null);
  const [isNewMember, setIsNewMember] = useState(false);
  const [memberId] = useState(1); // 임의의 memberId 설정

  return (
    <>
      <StatusBar hidden={true} />
      <NavigationContainer>
        {isLoggedIn ? (
          isNewMember ? (
            <RegisterProfileStack
              accessToken={accessToken}
              memberId={memberId}
              setIsNewMember={setIsNewMember}
            />
          ) : (
            <Tab.Navigator
              screenOptions={({route}) => ({
                headerShown: false,
                tabBarIcon: ({focused, color, size}) => {
                  let iconSource;

                  if (route.name === 'HomeStack') {
                    iconSource = require('./assets/images/icon_tab_home.png');
                  } else if (route.name === 'Community') {
                    iconSource = require('./assets/images/icon_tab_community.png');
                  } else if (route.name === 'InterestStack') {
                    iconSource = require('./assets/images/icon_interest_run.png');
                  } else if (route.name === 'My') {
                    iconSource = require('./assets/images/icon_tab_my.png');
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
                },
              })}
              initialRouteName="HomeStack">
              <Tab.Screen
                name="Community"
                options={{
                  tabBarLabel: 'Community',
                }}>
                {props => (
                  <CommunityScreen
                    {...props}
                    accessToken={accessToken}
                    memberId={memberId}
                  />
                )}
              </Tab.Screen>
              <Tab.Screen
                name="InterestStack"
                options={{
                  tabBarLabel: 'Interest',
                }}>
                {props => (
                  <InterestStack
                    {...props}
                    accessToken={accessToken}
                    memberId={memberId}
                  />
                )}
              </Tab.Screen>
              <Tab.Screen
                name="HomeStack"
                options={{
                  tabBarLabel: 'Home',
                }}>
                {props => (
                  <HomeStack
                    {...props}
                    accessToken={accessToken}
                    memberId={memberId}
                  />
                )}
              </Tab.Screen>
              <Tab.Screen
                name="My"
                options={{
                  tabBarLabel: 'My',
                }}>
                {props => (
                  <MyScreen
                    {...props}
                    accessToken={accessToken}
                    memberId={memberId}
                    setIsNewMember={setIsNewMember}
                  />
                )}
              </Tab.Screen>
            </Tab.Navigator>
          )
        ) : (
          <>
            <LoginScreen
              setIsLoggedIn={setIsLoggedIn}
              setAccessToken={setAccessToken}
              setIsNewMember={setIsNewMember}
            />
            <Button title="Skip Login" onPress={() => setIsLoggedIn(true)} />
          </>
        )}
      </NavigationContainer>
    </>
  );
};

export default App;
