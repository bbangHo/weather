# 날씨 톡톡 (Back-End)

> Spring Boot 기반 동네 날씨/커뮤니티 백엔드 (팀: FE 1, BE 2) — **운영 중**

## TL;DR
- 메인 날씨 API: 평균 **47~58%↓**, p95 **53~61%↓** (30/50/100 VU)
- 최초 응답시간 개선 **42%↓ (1150→664ms)** — 비동기 쓰기 분리 + JdbcTemplate 배치 쿼리 적용
- 자세히: 
  - [메인 화면 응답 속도 개선: 커넥션 대기 해소 + 캐시 전략 적용](/main-proof/README.md)
  - [날씨 API 응답속도 개선: 비동기 저장 로직 도입 전후 성능 비교](https://github.com/bbangHo/weather/wiki/%EB%82%A0%EC%94%A8-API-%EC%9D%91%EB%8B%B5%EC%86%8D%EB%8F%84-%EA%B0%9C%EC%84%A0:-%EB%B9%84%EB%8F%99%EA%B8%B0-%EC%A0%80%EC%9E%A5-%EB%A1%9C%EC%A7%81-%EB%8F%84%EC%9E%85-%EC%A0%84%ED%9B%84-%EC%84%B1%EB%8A%A5-%EB%B9%84%EA%B5%90)

## 빠른 바로가기
- **[Android 다운로드](https://play.google.com/store/apps/details?id=com.weather2&hl=ko)** | **[IOS 다운로드](https://apps.apple.com/kr/app/%EB%82%A0%EC%94%A8%ED%86%A1%ED%86%A1/id6738004972)**
- [메인 화면 응답속도 개선](/main-proof/README.md)
- [API 문서 (notion)](https://safe-scabiosa-656.notion.site/API-0eb8d352ebd44c04a42c9e6dcbfe15dc?source=copy_link) 
- [랜딩 페이지](https://safe-scabiosa-656.notion.site/17f2f55997ef802da016cca3592e600f?source=copy_link)

## 시스템 개요
기상청 데이터 기반의 예보 + 지역 커뮤니티. 캐시(프로세스 내 L1 + Redis L2)로 읽기 병목을 완화하고, 인기 지역은 사전 갱신/워밍업으로 콜드스타트를 줄였습니다.

![아키텍처](/docs/architecture.png)[config]()

## 성능 개선 하이라이트
| 항목 | Before | After |               개선 |
|---|---:|---:|-----------------:|
| 메인 날씨 API 평균(30/50/100VU) | 724/833/1327ms | 302/380/707ms |    **58/54/47%** |
| p95(30/50/100VU) | 1231/1535/2793ms | 479/669/1309ms |    **61/56/53%** |

- 자세히: **[메인 화면 응답속도 개선](/main-proof/README.md)**

## 주요 기능
- 날씨 예보 조회(24시간) / 체감 정보 제공(ex. '추움', '더움') / 인접 지역 최신 게시글 조회 / 무한 스크롤 피드 / 경험치 시스템 / 예보 알림
- 인증/인가(JWT), 회원/위치 관리

## 기술 스택
- **백엔드**: Java 17, Spring Boot 3.3.x, Spring Data JPA, QueryDSL
- **데이터베이스**: MySQL 8.0.x (RDS)
- **캐시**: Redis(싱글 노드), L1/L2 이중화, Cache-Aside 전략
- **인프라/배포**: AWS EC2/RDS/ALB/ElastiCache/S3, GitHub Actions + Docker
- **관측/테스트**: Logback 로깅, CloudWatch, k6

## 빠른 시작(로컬)
```bash
# 1) 환경변수(.env 예시: DB_URL, DB_USER, DB_PASS, REDIS_HOST)
cp .env.example .env

# 2) 빌드/테스트 (빌드 캐시 활성화)
./gradlew test -i --build-cache
./gradlew bootJar

# 3) 실행
java -jar build/libs/app.jar
