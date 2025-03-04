import React, {useEffect, useState} from 'react';
import {View, StyleSheet, Dimensions, ActivityIndicator} from 'react-native';
import Svg, {Path, Circle, Text as SvgText, TSpan} from 'react-native-svg';

const {width, height} = Dimensions.get('window');
const aspectRatio = height / width;

const graphWidth = aspectRatio < 2.09999 ? width * 0.95 : width * 0.95;

const graphHeight = 160;
const graphTopMargin = 20;

const WeatherGraph = ({
  accessToken,
  weatherData = {weatherPerHourList: [], temperature: {}},
  refreshing,
  onRefreshComplete,
}) => {
  const [temperatureData, setTemperatureData] = useState([]);
  const [maxTmp, setMaxTmp] = useState(0);
  const [minTmp, setMinTmp] = useState(0);

  const leftMargin = 40;

  const loadWeatherData = () => {
    if (
      weatherData &&
      weatherData.weatherPerHourList?.length > 0 &&
      weatherData.temperature
    ) {
      const tempData = weatherData.weatherPerHourList
        .slice(0, 13)
        .map(item => ({
          hour: item.hour || '',
          tmp: item.tmp ?? 0,
        }));

      if (tempData.length > 0) {
        const newMaxTmp = Math.max(...tempData.map(item => item.tmp));
        const newMinTmp = Math.min(...tempData.map(item => item.tmp));

        setMaxTmp(newMaxTmp);
        setMinTmp(newMinTmp);
      } else {
        setMaxTmp(0);
        setMinTmp(0);
      }

      setTemperatureData(tempData);
    } else {
      setTemperatureData([]);
    }

    if (onRefreshComplete) {
      onRefreshComplete();
    }
  };

  useEffect(() => {
    loadWeatherData();
  }, [weatherData]);

  useEffect(() => {
    if (refreshing) {
      loadWeatherData();
    }
  }, [refreshing]);

  const getX = index => {
    if (!temperatureData.length) return leftMargin;
    const interval =
      (graphWidth - leftMargin - 20) / (temperatureData.length - 1);
    return leftMargin + interval * index;
  };

  const getY = temperature => {
    let adjustedMax = maxTmp;
    let adjustedMin = minTmp;

    if (minTmp <= 5) {
      adjustedMax = maxTmp + 8;
      adjustedMin = minTmp - 3;
    } else if (minTmp >= 20) {
      adjustedMax = maxTmp + 3;
      adjustedMin = minTmp - 8;
    } else {
      adjustedMax = maxTmp + 4;
      adjustedMin = minTmp - 6;
    }

    if (adjustedMax === adjustedMin) return graphHeight / 2;
    const scale = (graphHeight - 40) / (adjustedMax - adjustedMin);
    return graphHeight - (temperature - adjustedMin) * scale - 20;
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

  if (!temperatureData.length) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#999999" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Svg height={graphHeight} width={graphWidth}>
        <Path
          d={`M ${leftMargin} 20 V ${graphHeight - 20}`}
          stroke="#fff"
          strokeWidth="1"
        />

        <Path
          d={`M ${leftMargin} ${graphTopMargin} H ${graphWidth - 20}`}
          stroke="#E0E0E0"
          strokeWidth="1"
          strokeDasharray="4, 2"
        />
        <SvgText
          x={leftMargin - 10}
          y={getY(maxTmp) - 15}
          fill="#333"
          fontSize="9"
          textAnchor="middle">
          <TSpan x={leftMargin - 20} dy="1em">
            최고
          </TSpan>
          <TSpan x={leftMargin - 26} dy="1em">
            ( {maxTmp}°)
          </TSpan>
        </SvgText>

        <SvgText
          x={leftMargin - 10}
          y={getY(minTmp) - 5}
          fill="#333"
          fontSize="9"
          textAnchor="middle">
          <TSpan x={leftMargin - 20} dy="1em">
            최저
          </TSpan>
          <TSpan x={leftMargin - 26} dy="1em">
            ( {minTmp}°)
          </TSpan>
        </SvgText>

        {pathData && (
          <Path
            d={`${pathData} L ${getX(temperatureData.length - 1)} ${
              graphHeight - 20
            } L ${leftMargin} ${graphHeight - 20} Z`}
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
