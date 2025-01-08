import React, {useEffect, useState} from 'react';
import {
  View,
  StyleSheet,
  Dimensions,
  ActivityIndicator,
  Text,
} from 'react-native';
import Svg, {Path, Circle, Text as SvgText, Rect} from 'react-native-svg';
import {fetchWeatherData} from '../api/api';

const {width, height} = Dimensions.get('window');
const aspectRatio = height / width;

const graphWidth = aspectRatio < 2.09999 ? width * 0.95 : width * 0.95;

const graphHeight = 160;

const WeatherGraph = ({accessToken}) => {
  const [temperatureData, setTemperatureData] = useState([]);
  const [maxTmp, setMaxTmp] = useState(0);
  const [minTmp, setMinTmp] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const getWeatherData = async () => {
      try {
        const weatherData = await fetchWeatherData(accessToken);
        console.log('Fetched weather data:', weatherData);
        if (weatherData.isSuccess) {
          const {maxTmp, minTmp} = weatherData.result.temperature;

          const tempData = weatherData.result.weatherPerHourList
            .slice(0, 12)
            .map((item, index) => ({
              hour: item.hour || '',
              tmp: item.tmp ?? 0,
            }));

          setMaxTmp(maxTmp ?? 0);
          setMinTmp(minTmp ?? 0);
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
    if (!temperatureData.length) return 20;
    const interval = (graphWidth - 40) / (temperatureData.length - 1);
    return 20 + interval * index;
  };

  const getY = temperature => {
    if (maxTmp === minTmp) return graphHeight / 2;
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
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#999999" />
      </View>
    );
  }

  if (!temperatureData.length) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyText}>불러올 데이터가 없습니다.</Text>
      </View>
    );
  }

  return (
    <View
      style={[
        styles.container,
        aspectRatio < 2.09999 && {
          paddingTop: 20,
          paddingBottom: -10,
        },
      ]}>
      <Svg height={graphHeight} width={graphWidth}>
        <Rect
          x="10"
          y="20"
          width={graphWidth - 20}
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
            key={`circle-${index}`}
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
            key={`hour-${index}`}
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
            key={`temp-${index}`}
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
    marginBottom: 10,
    paddingVertical: 10,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 20,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 20,
  },
  emptyText: {
    color: '#777',
    fontSize: 14,
  },
});

export default WeatherGraph;
