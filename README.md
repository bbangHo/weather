> [Android 다운로드](https://play.google.com/store/apps/details?id=com.weather2&hl=ko)

> [IOS 다운로드](https://apps.apple.com/kr/app/%EB%82%A0%EC%94%A8%ED%86%A1%ED%86%A1/id6738004972)

# 날씨 톡톡 (Back-End)

Spring Boot 기반의 날씨 예보 앱 서비스 백엔드입니다. (3인 프로젝트 FE 1, BE 2)  
기상청 데이터를 활용하여 실시간 날씨 정보를 제공하며, 사용자 피드백을 수집하고 날씨 기반 커뮤니티 기능을 제공합니다.  
운영 중인 앱의 백엔드 성능 최적화, Git Actions + docker 배포 자동화 구축, 인프라 구성까지 직접 수행한 실전 프로젝트입니다.

---

## 🙋‍♂️ 기여자
| 이름 | 역할 |
|------|------|
| 손영호 |	백엔드 설계 및 개발 / 인프라 구성 / 배포 자동화 구축
| 성시영 |	백엔드 설계 및 개발 / 모니터링 설정
| 고주리 |	프론트엔드 개발

---

## ⚙️ 성능 최적화

- 비동기 저장 로직 도입 → API 응답 속도 **42% 개선** (1150ms → 664ms) [자세히](https://github.com/bbangHo/weather/wiki/%EB%82%A0%EC%94%A8-API-%EC%9D%91%EB%8B%B5%EC%86%8D%EB%8F%84-%EA%B0%9C%EC%84%A0:-%EB%B9%84%EB%8F%99%EA%B8%B0-%EC%A0%80%EC%9E%A5-%EB%A1%9C%EC%A7%81-%EB%8F%84%EC%9E%85-%EC%A0%84%ED%9B%84-%EC%84%B1%EB%8A%A5-%EB%B9%84%EA%B5%90)
- JdbcTemplate + 벌크 쿼리로 저장 성능 **55% 향상**  
- AOP 기반 로깅
- k6 기반 부하 테스트 스크립트 작성 및 결과 분석

---

## 🧰 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| Language | Java 17 |
| Framework | Spring Boot, Spring Data JPA |
| DB | MySQL, RDS (AWS) |
| Infra | AWS EC2, RDS, ALB, CloudWatch |
| CI/CD | GitHub Actions + Docker |
| 기타 | QueryDSL, AOP, k6 (성능 테스트) |

:? 기술 선택의 이유

---

## 🛰️ 인프라 구성도

- AWS EC2: Spring Boot
- RDS: MySQL
- ALB + HTTPS
- GitHub Actions + Docker 배포 자동화

---

# 날씨 톡톡이란?

> **실시간 날씨 경험을 공유하는 커뮤니티 플랫폼**
> 
> 
> 단순한 수치로만 제공되는 기온이 아닌, **사용자의 실제 체감 온도 및 지역 기반 커뮤니티 기능**을 통해 보다 **직관적인 날씨 정보를 제공**합니다. 
>

# 왜 이 앱을 만들었을까?

일반적인 날씨 앱은 기온, 습도, 강수량을 숫자로만 제공하여 실제 체감 온도를 이해하기 어렵습니다.

예를 들어, **기온이 25도**여도 **습도가 높으면 무덥게**, **바람이 강하면 서늘하게** 느껴질 수 있습니다.

또한, 전국 단위 날씨 예보는 **내 동네의 실제 날씨를 정확히 반영하지 못하는 문제**가 있습니다.

우리는 **사용자가 직접 체감 온도를 공유**하고, 이를 통해 **더 현실적인 날씨 정보를 얻을 수 있도록 돕는 앱을 개발**하였습니다.

> "숫자보다 직관적인 날씨 정보, 지역 주민이 직접 공유하는 신뢰도 높은 날씨 정보!"
> 
> 
> **날씨 톡톡은 날씨의 새로운 기준을 제시합니다.**
>

| ![이미지1](https://github.com/user-attachments/assets/b8b7084b-3f73-45fc-8eae-915dd6e912d8) | ![이미지2](https://github.com/user-attachments/assets/2031ee14-b28b-43d9-a9b3-a31f168dac68) | ![이미지3](https://github.com/user-attachments/assets/049bce19-28db-49a2-8e3c-cf298523f35b) | ![이미지4](https://github.com/user-attachments/assets/235dec11-8f3c-4c3d-8809-f8ba05e8cb89) | ![이미지5](https://github.com/user-attachments/assets/ea4acf4c-48fc-477b-86b6-a99e70129198) |
|---|---|---|---|---|


