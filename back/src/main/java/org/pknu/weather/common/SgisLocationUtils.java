package org.pknu.weather.common;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.feignClient.SgisClient;
import org.pknu.weather.common.feignClient.dto.SgisAccessTokenResponseDTO;
import org.pknu.weather.common.feignClient.dto.SgisLocationResponseDTO;
import org.pknu.weather.common.feignClient.dto.SgisLocationWithCoorResponseDTO;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


@Component
@RequiredArgsConstructor
public class SgisLocationUtils {

    private final SgisClient sgisClient;
    private String accessToken;
    private LocalDateTime expTime;

    @Value("${CONSUMER_KEY}")
    String consumerKey;

    @Value("${CONSUMER_SECRET}")
    String consumerSecret;


    public LocationDTO getAddressInfo( double x, double y){

        checkAccessToken();

        LocationDTO locationDTO = new LocationDTO();
        getAddressName(x,y,locationDTO);
        getAddressCoor(locationDTO.getFullAddress(), locationDTO);
        return locationDTO;

    }

    public void getAddressName( double x, double y, LocationDTO locationDTO){

        SgisLocationResponseDTO location = sgisClient.convertToLocationName(accessToken, x, y, 20);
        SgisLocationResponseDTO.Result addressInfo = location.getResult().get(0);

        locationDTO.setProvince(addressInfo.getSido_nm());
        locationDTO.setCity(addressInfo.getSgg_nm());
        locationDTO.setStreet(addressInfo.getEmdong_nm());

    }

    public void getAddressCoor(String address, LocationDTO locationDTO) {
        SgisLocationWithCoorResponseDTO locationCoor = sgisClient.getLocationCoor(accessToken, address);
        SgisLocationWithCoorResponseDTO.ResultData coor = locationCoor.getResult().getResultdata().get(0);

        locationDTO.setLongitude(coor.getX());
        locationDTO.setLatitude(coor.getY());
    }

    public void checkAccessToken(){
        if (accessToken == null || accessToken.isEmpty() || expTime.isAfter(LocalDateTime.now().minusMinutes(10)))
            getSgisAccessToken();
    }

    public synchronized void getSgisAccessToken(){
        SgisAccessTokenResponseDTO sgisAccessToken = sgisClient.getSgisAccessToken(consumerKey,consumerSecret);

        switch (sgisAccessToken.getErrCd()){
            case (401):
                throw new GeneralException(ErrorStatus._SGIS_BAD_AUTHENTICATION_PARAMETER);
            case (100):
                throw new GeneralException(ErrorStatus._SGIS_NOT_FOUND_RESULT);
            case(0):
                this.accessToken = sgisAccessToken.getResult().getAccessToken();
                this.expTime = convertMillis(sgisAccessToken.getResult().getAccessTimeout());
                break;
            default:
                throw new RuntimeException();
        }
    }

    public LocalDateTime convertMillis(long timeMillis){
        Instant instant = Instant.ofEpochMilli(timeMillis);
        ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());
        return dateTime.toLocalDateTime();
    }
}
