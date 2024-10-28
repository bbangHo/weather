const BASE_URL = 'http://13.125.128.147:8080';
import AsyncStorage from '@react-native-async-storage/async-storage';

export const sendAccessTokenToBackend = async accessToken => {
  try {
    console.log('Sending access token to backend...');
    console.log('Request URL:', `${BASE_URL}/token`);
    console.log('Access token:', accessToken);

    const response = await fetch(`${BASE_URL}/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({accessToken}),
    });

    console.log('Response status:', response.status);

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Failed to send access token:', response.status, errorText);
      throw new Error(errorText || 'Failed to send access token to backend');
    }

    const data = await response.json();

    console.log('Backend response:', data);

    if (!data.isSuccess) {
      console.error('Backend error:', data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data;
  } catch (error) {
    console.error('Error sending access token:', error.message);
    throw error;
  }
};

export const refreshAccessToken = async () => {
  try {
    const accessToken = await AsyncStorage.getItem('accessToken');
    const refreshToken = await AsyncStorage.getItem('refreshToken');

    console.log('Retrieved accessToken:', accessToken);
    console.log('Retrieved refreshToken:', refreshToken);

    if (!accessToken || !refreshToken) {
      throw new Error('No access token or refresh token found');
    }

    const response = await fetch(`${BASE_URL}/refreshToken`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({accessToken, refreshToken}),
    });

    const result = await response.json();

    console.log('Refresh Token API response:', JSON.stringify(result, null, 2));

    if (result.isSuccess) {
      await AsyncStorage.setItem('accessToken', result.result.accessToken);
      return result.result.accessToken;
    } else {
      throw new Error(result.message || 'Failed to refresh access token');
    }
  } catch (err) {
    console.error('Failed to refresh access token:', err);
    throw err;
  }
};

export const fetchWeatherData = async (memberId, accessToken) => {
  try {
    const response = await fetch(
      `${BASE_URL}/api/v1/main/weather?memberId=${memberId}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
      },
    );

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to fetch weather data:',
        response.status,
        errorText,
      );
      throw new Error(`Failed to fetch weather data: ${errorText}`);
    }

    const data = await response.json();
    console.log('Backend response:', data);

    if (!data.isSuccess) {
      console.error('Backend error:', data.code, data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    if (data.result && data.result.weatherPerHourList) {
      console.log(
        'First item in weatherPerHourList:',
        data.result.weatherPerHourList[0],
      );
    }

    return data;
  } catch (error) {
    console.error('Error fetching weather data:', error.message);
    throw error;
  }
};

export const sendLocationToBackend = async (
  longitude,
  latitude,
  accessToken,
) => {
  try {
    const postData = {latitude, longitude};

    console.log('Sending location data:', postData);

    const response = await fetch(`${BASE_URL}/api/v1/location/coor`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(postData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to send location data:',
        response.status,
        errorText,
      );
      throw new Error(errorText || 'Failed to send location data to backend');
    }

    const data = await response.json();

    if (!data.isSuccess) {
      console.error('Backend error:', data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data;
  } catch (error) {
    console.error('Error sending location data:', error.message);
    throw error;
  }
};

export const createPost = async (postData, accessToken, memberId) => {
  const url = `${BASE_URL}/api/v1/post?memberId=${memberId}`;

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(postData),
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error('Failed to create post:', response.status, errorResponse);
      throw new Error('Failed to create post');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error creating post:', error);
    throw error;
  }
};

export const createInterestPost = async (postData, accessToken, memberId) => {
  const url = `${BASE_URL}/api/v1/post/hobby?memberId=${memberId}`;

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(postData),
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error(
        'Failed to create interest post:',
        response.status,
        errorResponse,
      );
      throw new Error('Failed to create interest post');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error creating interest post:', error);
    throw error;
  }
};

export const fetchPosts = async (
  accessToken,
  memberId,
  postType = 'WEATHER',
  lastPostId = null,
) => {
  try {
    let url = `${BASE_URL}/api/v1/community/posts?memberId=${memberId}&size=6&postType=${postType}`;

    if (lastPostId !== null) {
      url += `&lastPostId=${lastPostId}`;
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Failed to fetch posts:', response.status, errorText);
      throw new Error('Failed to fetch posts');
    }

    const data = await response.json();
    return data.result?.postList || [];
  } catch (error) {
    console.error('Error fetching posts:', error);
    throw error;
  }
};

export const fetchPopularPosts = async (
  accessToken,
  memberId,
  lastPostId = null,
  size = 10,
) => {
  try {
    let url = `${BASE_URL}/api/v1/main/posts/popular?memberId=${memberId}&size=${size}`;

    if (lastPostId) {
      url += `&lastPostId=${lastPostId}`;
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error(
        'Failed to fetch popular posts:',
        response.status,
        errorResponse,
      );
      throw new Error('Failed to fetch popular posts');
    }

    const data = await response.json();
    return data.result;
  } catch (error) {
    console.error('Error fetching popular posts:', error);
    throw error;
  }
};

export const toggleLikePost = async (accessToken, memberId, postId) => {
  try {
    const url = `${BASE_URL}/api/v1/post/recommendation?memberId=${memberId}&postId=${postId}`;

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error('Failed to like post:', response.status, errorResponse);
      throw new Error('Failed to like post');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error liking post:', error);
    throw error;
  }
};

export const fetchWeatherTags = async (accessToken, memberId) => {
  try {
    const url = `${BASE_URL}/api/v1/main/weather/simple/tags?memberId=${memberId}`;

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to fetch weather tags:',
        response.status,
        errorText,
      );
      throw new Error('Failed to fetch weather tags');
    }

    const data = await response.json();
    return data.result;
  } catch (error) {
    console.error('Error fetching weather tags:', error);
    throw error;
  }
};

export const fetchPostTags = async accessToken => {
  const url = `${BASE_URL}/api/v1/tags`;

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorResponse = await response.text();
      console.error('Failed to fetch tags:', response.status, errorResponse);
      throw new Error('Failed to fetch tags');
    }

    const data = await response.json();
    return data.result;
  } catch (error) {
    console.error('Error fetching tags:', error);
    throw error;
  }
};

export const fetchRainForecast = async (accessToken, memberId) => {
  try {
    const url = `${BASE_URL}/api/v1/main/weather/simple/rain?memberId=${memberId}`;

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to fetch rain forecast:',
        response.status,
        errorText,
      );
      throw new Error('Failed to fetch rain forecast');
    }

    const data = await response.json();
    if (!data.isSuccess) {
      console.error('Backend error:', data.code, data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data.result;
  } catch (error) {
    console.error('Error fetching rain forecast:', error.message);
    throw error;
  }
};

export const fetchUserLocation = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/location/defaultLoc`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(
        'Failed to fetch user location:',
        response.status,
        errorText,
      );
      throw new Error(`Failed to fetch user location: ${errorText}`);
    }

    const data = await response.json();
    if (!data.isSuccess) {
      console.error('Backend error:', data.message);
      throw new Error(data.message || 'Unknown error from backend');
    }

    return data.result;
  } catch (error) {
    console.error('Error fetching user location:', error.message);
    throw error;
  }
};

export const fetchExtraWeatherInfo = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/main/extraWeatherInfo`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    const result = await response.json();
    if (result.isSuccess) {
      return result.result;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Error fetching extra weather info:', error);
    throw error;
  }
};

export const fetchMemberInfo = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/member/info`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    const result = await response.json();
    if (result.isSuccess) {
      return result.result;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Error fetching member info:', error);
    throw error;
  }
};

export const registerProfile = async (
  nickname,
  sensitivity,
  profileImage,
  accessToken,
  memberId,
) => {
  const formData = new FormData();

  formData.append('nickname', nickname);
  formData.append('sensitivity', sensitivity);

  if (profileImage) {
    formData.append('profileImg', {
      uri: profileImage.uri,
      type: profileImage.type || 'image/jpeg',
      name: profileImage.name || 'profile.jpg',
    });
  }

  formData._parts.forEach(part => {
    console.log(`FormData key: ${part[0]}, value: ${JSON.stringify(part[1])}`);
  });

  console.log('Profile Image Details:', {
    uri: profileImage.uri,
    type: profileImage.type,
    name: profileImage.name,
  });

  try {
    const response = await fetch(`${BASE_URL}/api/v1/member/info`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        memberId: memberId,
      },
      body: formData,
    });

    const responseData = await response.json();

    if (!response.ok) {
      console.error('Backend error:', responseData);
      throw new Error(responseData.message || '프로필 저장 실패');
    }

    return responseData;
  } catch (error) {
    console.error('API 요청 실패:', error);
    throw error;
  }
};

/*
export const registerProfile = async (
  nickname,
  profileImage,
  accessToken,
  memberId,
) => {
  const formData = new FormData();
  formData.append('nickname', nickname);

  if (profileImage) {
    formData.append('profileImage', {
      uri: profileImage.uri,
      name: profileImage.fileName,
      type: profileImage.type,
    });
  }

  try {
    const response = await fetch(`${BASE_URL}/api/v1/member/info`, {
      method: 'POST',
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${accessToken}`,
        memberId: memberId,
      },
      body: formData,
    });

    const responseData = await response.json();

    if (!response.ok) {
      console.error('Backend error:', responseData);
      throw new Error(responseData.message || '프로필 저장 실패');
    }

    return responseData;
  } catch (error) {
    console.error('API 요청 실패:', error);
    throw error;
  }
};
*/

export const fetchLocationInfo = async (
  accessToken,
  memberId,
  province = '',
  city = '',
) => {
  const url = `${BASE_URL}/api/v1/location/locationInfo?province=${province}&city=${city}`;

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      console.error(
        'Failed to fetch location info:',
        response.status,
        errorResponse,
      );
      throw new Error('Failed to fetch location info');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching location info:', error);
    throw error;
  }
};
