import React, {useState} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createStackNavigator} from '@react-navigation/stack';
import HomeScreen from './src/screens/HomeScreen';
import CommunityScreen from './src/screens/CommunityScreen';
import MyScreen from './src/screens/MyScreen';
import PostCreationScreen from './src/screens/PostCreationScreen';
import LoginScreen from './src/screens/LoginScreen';
import Icon from 'react-native-vector-icons/Ionicons';
import {StatusBar, Button} from 'react-native';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const HomeStack = () => (
  <Stack.Navigator screenOptions={{headerShown: false}}>
    <Stack.Screen name="Home" component={HomeScreen} />
    <Stack.Screen name="PostCreationScreen" component={PostCreationScreen} />
  </Stack.Navigator>
);

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [accessToken, setAccessToken] = useState(null);

  return (
    <>
      <StatusBar hidden={true} />
      <NavigationContainer>
        {isLoggedIn ? (
          <Tab.Navigator screenOptions={{headerShown: false}}>
            <Tab.Screen
              name="Community"
              component={CommunityScreen}
              options={{
                tabBarIcon: ({color, size}) => (
                  <Icon name="people" color={color} size={size} />
                ),
              }}
            />
            <Tab.Screen
              name="HomeStack"
              component={HomeStack}
              options={{
                tabBarIcon: ({color, size}) => (
                  <Icon name="home" color={color} size={size} />
                ),
                tabBarLabel: 'Home',
              }}
            />
            <Tab.Screen
              name="My"
              component={MyScreen}
              options={{
                tabBarIcon: ({color, size}) => (
                  <Icon name="person" color={color} size={size} />
                ),
              }}
            />
          </Tab.Navigator>
        ) : (
          <>
            <LoginScreen
              setIsLoggedIn={setIsLoggedIn}
              setAccessToken={setAccessToken}
            />
            <Button title="Skip Login" onPress={() => setIsLoggedIn(true)} />
          </>
        )}
      </NavigationContainer>
    </>
  );
};

export default App;
