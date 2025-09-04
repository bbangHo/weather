import http from "k6/http";
import {check, sleep, group } from "k6";
import {users} from './users.js';

const URL = 'https://weather-community.shop';

export const options = {
    scenarios: {
        spike_test_1: {
            executor: "per-vu-iterations", // VU 수만큼 1회 실행 후 종료
            vus: 100,                      // 동시 유저
            iterations: 1,                // 각 유저는 1회만 실행
            startTime: "0s",                // 해당 시각에 시나리오 시작
            maxDuration: "30s",           // 30초 안에 끝내기
        },
    },
    thresholds: {
        http_req_duration: [
            'avg<500',
            'p(95)<2000',
        ],
        'http_req_duration{endpoint:/api/v1/attendance/check-in}': ['avg<500', 'p(95)<2000'],
        'http_req_duration{endpoint:/api/v1/main/weather}': ['avg<500', 'p(95)<2000'],
        'http_req_duration{endpoint:/api/v1/location/defaultLoc}': ['avg<500', 'p(95)<2000'],
        'http_req_duration{endpoint:/api/v1/member/info}': ['avg<500', 'p(95)<2000'],
        'http_req_duration{endpoint:/api/v1/location/coor}': ['avg<500', 'p(95)<2000'],
        'http_req_duration{endpoint:/api/v1/main/posts/popular}': ['avg<500', 'p(95)<2000'],
        'http_req_duration{endpoint:/api/v1/main/weather/simple/rain}': ['avg<500', 'p(95)<2000'],
        'http_req_duration{endpoint:/api/v1/main/weather/simple/tags}': ['avg<500', 'p(95)<2000'],
    },
};

// 각 VU에 토큰 할당
export default function () {
    const index = (__VU - 1) % users.length;
    const {token, nickname, latitude, longitude} = users[index];

    const params = {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
    };

    const point = JSON.stringify({
        latitude: latitude,
        longitude: longitude
    });

    // 메인 페이지 API 들
    group('메인화면 진입', () => {
        getMemberInfo(params);
        getMemberInfo(params);
        getUserLoc(params);
        getUserLoc(params);
        getWeather(params);
        checkIn(params);
        saveUserLoc(point, params);
        getPosts(params);
        getRainProb(params);
        getSimpleTag(params);
    });

    // // ️20% 확률로 게시글 생성
    // if (Math.random() < 0.23) {
    //     group('게시글 생성', () => {
    //         const selectedTagsRes = readyCreateWeather(params);
    //         const body = JSON.stringify({
    //             content: "성능 테스트 시나리오 게시글 생성",
    //             temperatureTagCode: 1,
    //             skyTagCode: selectedTagsRes.result.SkyTag.find(tag => tag.selected)?.code,
    //             humidityTagCode: selectedTagsRes.result.HumidityTag.find(tag => tag.selected)?.code,
    //             windTagCode: selectedTagsRes.result.WindTag.find(tag => tag.selected)?.code,
    //             dustTagCode: selectedTagsRes.result.DustTag.find(tag => tag.selected)?.code
    //         });
    //
    //         console.log(`게시글 생성 user${index}`);
    //         postWeather(body, params);
    //     });
    // }
}

function checkIn(params) {
    const res = http.post(URL + "/api/v1/attendance/check-in", null, {
        ...params,
        tags: { endpoint: '/api/v1/attendance/check-in' },
    });

    check(res.json() || {}, {
        "checkIn: code is 200": (r) => r.code === "200",
    });
}

function readyCreateWeather(params) {
    const res = http.get(URL + "/api/v1/selected-tags", {
        ...params,
        tags: { endpoint: '/api/v1/selected-tags' },
    });

    check(res.json() || {}, {
        "readyCreateWeather: code is 200": (r) => r.code === "200",
    });

    return res.json();
}

function postWeather(body, params) {
    const res = http.post(URL + "/api/v1/post", body, {
        ...params,
        tags: { endpoint: '/api/v1/post' },
    });

    check(res.json() || {}, {
        "postWeather: code is 200": (r) => r.code === "200",
    });

    return res.json();
}

function getWeather(params) {
    const res = http.get(URL + "/api/v1/main/weather", {
        ...params,
        tags: { endpoint: '/api/v1/main/weather' },
    });
    const body = res.json() || {};  // JSON 파싱된 응답

    if (!check(body, {
        "getWeather: code is 200": (r) => r.code === "200",
    })) {
        console.error(
            `FAIL - status: ${res.status}, duration: ${res.timings.duration}ms\n` +
            `message: ${body.message}\n` +
            `result (object): ${body.result}\n` +
            `result (stringified): ${JSON.stringify(body.result)}`
        );
    }
}

function getUserLoc(params) {
    const res = http.get(URL + "/api/v1/location/defaultLoc", {
        ...params,
        tags: { endpoint: '/api/v1/location/defaultLoc' },
    });
    check(res.json() || {}, {
        "getUserLoc: code is 200": (r) => r.code === "200",
    });
}

function getMemberInfo(params) {
    const res = http.get(URL + "/api/v1/member/info", {
        ...params,
        tags: { endpoint: '/api/v1/member/info' },
    });
    check(res.json() || {}, {
        "getMemberInfo: code is 200": (r) => r.code === "200",
    });
}

function saveUserLoc(point, params) {
    const res = http.post(URL + "/api/v1/location/coor", point, {
        ...params,
        tags: { endpoint: '/api/v1/location/coor' },
    });
    check(res.json() || {}, {
        "saveUserLoc: code is 200": (r) => r.code === "200",
    });
}

function getPosts(params) {
    const res = http.get(URL + "/api/v1/main/posts/popular", {
        ...params,
        tags: { endpoint: '/api/v1/main/posts/popular' },
    });

    check(res.json() || {}, {
        "getPosts: code is 200": (r) => r.code === "200",
    });
}

function getRainProb(params) {
    const res = http.get(URL + "/api/v1/main/weather/simple/rain", {
        ...params,
        tags: { endpoint: '/api/v1/main/weather/simple/rain' },
    });

    check(res.json() || {}, {
        "getRainProb: code is 200": (r) => r.code === "200",
    });
}

function getSimpleTag(params) {
    const res = http.get(URL + "/api/v1/main/weather/simple/tags", {
        ...params,
        tags: { endpoint: '/api/v1/main/weather/simple/tags' },
    });

    check(res.json() || {}, {
        "getSimpleTag: code is 200": (r) => r.code === "200" || r.code === "TAG_404_2",
    });
}


