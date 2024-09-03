const BASE_URL = 'http://13.125.128.147:8080';

export const sendAccessTokenToBackend = async accessToken => {
  try {
    const response = await fetch(`${BASE_URL}/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({accessToken}),
    });

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

    return data;
  } catch (error) {
    console.error('Error fetching weather data:', error.message);
    throw error;
  }
};

export const sendLocationToBackend = async (
  latitude,
  longitude,
  accessToken,
) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/location/coor`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify({latitude, longitude}),
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
    console.log('Backend response:', data);

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
