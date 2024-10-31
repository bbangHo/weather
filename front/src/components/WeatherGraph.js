import React, {useEffect, useState} from 'react';
import {View, StyleSheet, Dimensions, ActivityIndicator} from 'react-native';
import Svg, {Path, Line, Text as SvgText} from 'react-native-svg';
import globalStyles from '../globalStyles';
import {fetchWeatherData} from '../api/api';

const WeatherGraph = ({accessToken}) => {
  const {width} = Dimensions.get('window');
  const graphWidth = width - 40;
  const graphHeight = 200;
  const paddingLeft = 60;

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
        } else {
          console.error('Failed to fetch weather data:', weatherData.message);
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
    if (temperatureData.length === 0) return 0;
    const interval = (graphWidth - paddingLeft) / (temperatureData.length - 1);
    return paddingLeft + interval * index;
  };

  const getY = temperature => {
    if (maxTmp === null || minTmp === null || temperature === null) return 0;
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
    const hours = date.getHours().toString().padStart(2, '0');
    return `${hours}시`;
  };

  if (loading) {
    return <ActivityIndicator size="large" color="#fff" />;
  }

  return (
    <View style={[styles.container, globalStyles.transparentBackground]}>
      <Svg height={graphHeight} width={graphWidth}>
        {maxTmp !== null && (
          <>
            <Line
              x1={paddingLeft}
              y1={getY(maxTmp)}
              x2={graphWidth}
              y2={getY(maxTmp)}
              stroke="white"
              strokeWidth="0.5"
            />
            <SvgText
              x="0"
              y={getY(maxTmp) + 5}
              fill="white"
              fontSize="10"
              fontWeight="bold">
              최고({maxTmp}°C)
            </SvgText>
          </>
        )}

        {minTmp !== null && (
          <>
            <Line
              x1={paddingLeft}
              y1={getY(minTmp)}
              x2={graphWidth}
              y2={getY(minTmp)}
              stroke="white"
              strokeWidth="0.5"
            />
            <SvgText
              x="0"
              y={getY(minTmp) + 5}
              fill="white"
              fontSize="10"
              fontWeight="bold">
              최저({minTmp}°C)
            </SvgText>
          </>
        )}

        {temperatureData.length > 0 &&
          temperatureData.map((item, index) => (
            <SvgText
              key={index}
              x={getX(index)}
              y={graphHeight - 5}
              fill="white"
              fontSize="10"
              textAnchor="middle">
              {formatHour(item.hour)}
            </SvgText>
          ))}

        {temperatureData.length > 0 && (
          <Path d={pathData} fill="none" stroke="white" strokeWidth="2" />
        )}
      </Svg>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    marginVertical: 10,
    borderRadius: 10,
    padding: 20,
  },
  text: {
    color: '#fff',
    marginBottom: 10,
  },
});

export default WeatherGraph;
