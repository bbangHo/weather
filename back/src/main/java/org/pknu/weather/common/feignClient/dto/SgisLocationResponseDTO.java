package org.pknu.weather.common.feignClient.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SgisLocationResponseDTO {

    private String id;
    private List<Result> result;
    private String errMsg;
    private int errCd;

    @Getter
    @Setter
    public static class Result {
        private String sido_nm;
        private String sgg_nm;
        private String emdong_nm;
    }
}

