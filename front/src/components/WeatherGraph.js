import React, {useEffect, useState} from 'react';
import {View, StyleSheet, Dimensions, ActivityIndicator} from 'react-native';
import Svg, {Path, Circle, Text as SvgText, Rect} from 'react-native-svg';
import {fetchWeatherData} from '../api/api';

const {width} = Dimensions.get('window');
const graphWidth = width - 40;
const graphHeight = 160;

const WeatherGraph = ({accessToken}) => {
  const [temperatureData, setTemperatureData] = useState([]);
  const [maxTmp, setMaxTmp] = useState(null);
  const [minTmp, setMinTmp] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const getWeatherData = async () => {
      try {
        const weatherData = await fetchWeatherData(accessToken);
        if (weatherData.isSuccess) {
          setMaxTmp(weatherData.result.temperature.maxTmp);
          setMinTmp(weatherData.result.temperature.minTmp);

          const tempData = weatherData.result.weatherPerHourList
            .slice(0, 12)
            .map(item => ({
              hour: item.hour,
              tmp: item.tmp,
            }));
          setTemperatureData(tempData);
        }
      } catch (error) {
        console.error('Error fetching weather data:', error.message);
      } finally {
        setLoading(false);
      }
    };

    getWeatherData();
  }, [accessToken]);

  const getX = index => {
    const interval = (graphWidth - 40) / (temperatureData.length - 1);
    return 20 + interval * index;
  };

  const getY = temperature => {
    if (!maxTmp || !minTmp) return 0;
    const scale = (graphHeight - 40) / (maxTmp - minTmp);
    return graphHeight - (temperature - minTmp) * scale - 20;
  };

  const pathData =
    temperatureData.length > 0
      ? temperatureData
          .map((tempData, index) => {
            const x = getX(index);
            const y = getY(tempData.tmp);
            return `${index === 0 ? 'M' : 'L'} ${x} ${y}`;
          })
          .join(' ')
      : '';

  const formatHour = isoString => {
    const date = new Date(isoString);
    return `${date.getHours()}시`;
  };

  if (loading) {
    return <ActivityIndicator size="large" color="#999999" />;
  }

  return (
    <View style={styles.container}>
      <Svg height={graphHeight} width={graphWidth}>
        <Rect
          x="20"
          y="20"
          width={graphWidth - 40}
          height={graphHeight - 40}
          fill="#fff"
          rx="10"
        />

        {pathData && (
          <Path
            d={`${pathData} L ${getX(temperatureData.length - 1)} ${
              graphHeight - 20
            } L 20 ${graphHeight - 20} Z`}
            fill="rgba(63, 125, 253, 0.3)"
          />
        )}

        {pathData && (
          <Path d={pathData} fill="none" stroke="#3f7dfd" strokeWidth="2" />
        )}

        {temperatureData.map((item, index) => (
          <Circle
            key={index}
            cx={getX(index)}
            cy={getY(item.tmp)}
            r="4.5"
            fill="#3f7dfd"
            stroke="#fff"
            strokeWidth="1"
          />
        ))}

        {temperatureData.map((item, index) => (
          <SvgText
            key={index}
            x={getX(index)}
            y={graphHeight - 5}
            fill="#777"
            fontSize="10"
            textAnchor="middle">
            {formatHour(item.hour)}
          </SvgText>
        ))}

        {temperatureData.map((item, index) => (
          <SvgText
            key={index}
            x={getX(index)}
            y={getY(item.tmp) - 10}
            fill="#333"
            fontSize="10"
            fontWeight="bold"
            textAnchor="middle">
            {item.tmp}°
          </SvgText>
        ))}
      </Svg>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    borderRadius: 10,
    marginHorizontal: 10,
    marginTop: 10,
    marginBottom: 20,
    paddingVertical: 5,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 6},
    shadowOpacity: 0.1,
    shadowRadius: 15,
    elevation: 6,
  },
});

export default WeatherGraph;
