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
import Icon from 'react-native-vector-icons/Ionicons';
import {StatusBar, Button} from 'react-native';

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
            <RegisterProfileScreen
              accessToken={accessToken}
              memberId={memberId}
              setIsNewMember={setIsNewMember}
            />
          ) : (
            <Tab.Navigator screenOptions={{headerShown: false}}>
              <Tab.Screen
                name="Community"
                options={{
                  tabBarIcon: ({color, size}) => (
                    <Icon name="people" color={color} size={size} />
                  ),
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
                name="Interest"
                options={{
                  tabBarIcon: ({color, size}) => (
                    <Icon name="book" color={color} size={size} />
                  ),
                  tabBarLabel: 'Interest',
                }}>
                {props => (
                  <InterestScreen
                    {...props}
                    accessToken={accessToken}
                    memberId={memberId}
                  />
                )}
              </Tab.Screen>
              <Tab.Screen
                name="HomeStack"
                options={{
                  tabBarIcon: ({color, size}) => (
                    <Icon name="home" color={color} size={size} />
                  ),
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
                  tabBarIcon: ({color, size}) => (
                    <Icon name="person" color={color} size={size} />
                  ),
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
