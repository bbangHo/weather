package org.pknu.weather.repository;

/*
@SpringBootTest
class LocationRepositoryTest {

    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    LocationRepository locationRepository;

    @BeforeEach
    void init() {
        Location location = Location.builder()
                .city("city")
                .province("province")
                .street("street")
                .latitude(30.0)
                .longitude(60.0)
                .build();

        List<Weather> weatherList = new ArrayList<>();

        // 어제, 오늘 날씨 추가
        for(int i = 0; i <= 23; i++) {
            Weather weather = Weather.builder()
                    .basetime(LocalDateTime.now())
                    .presentationTime(
                            DateTimeFormatter.formattedDateTime2LocalDateTime(DateTimeFormatter.getFormattedDate(),
                            DateTimeFormatter.getFormattedTimeByOneHour(LocalTime.of(i, 0))))
                    .build();

            LocalDateTime now = LocalDateTime.now();
            Weather yesterday = Weather.builder()
                    .basetime(now)
                    .presentationTime(
                            DateTimeFormatter.formattedDateTime2LocalDateTime(
                                    DateTimeFormatter.getFormattedDate(LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth() - 1)),
                                    DateTimeFormatter.getFormattedTimeByOneHour(LocalTime.of(i, 0))))
                    .build();

            weather.addLocation(location);
            weatherList.add(weather);

            yesterday.addLocation(location);
            weatherList.add(yesterday);
        }

        System.out.println("##################"+weatherList.size());
        weatherRepository.saveAll(weatherList);
    }

    @Test
    @Transactional
    @DisplayName("지역과 날씨를 페치조인 하는 테스트. 날씨는 미래의 날씨만 가져온다.")
    void locationAndWeatherFetchJoinTest() {
        // given
        Location location = locationRepository.findAll().get(0);
        int eq = 24 - LocalTime.now().getHour() - 1;

        // when
        List<Weather> weathers = weatherRepository.findAllWithLocation(location, LocalDateTime.now().plusHours(24));

        // then
        assertThat(weathers.size()).isEqualTo(eq);
        assertThat(weathers.get(0).getLocation().getId()).isEqualTo(location.getId());
    }
}

 */
