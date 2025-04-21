## 01. 프로젝트 설명

- Spring + MySQL의 Spatial Index를 적용한 개인 프로젝트

## 02. 기능

- Spring에서 MySQL의 공간 데이터 적용 및 생성
- Spring에서 MySQL의 공간 인덱스를 활용한 조회
- 공간 인덱스를 타지 않은 조회, 공간 인덱스 조회의 성능 비교

## 03. 사용 기술

- `Spring Boot 3.2`, `Spring Data JPA`
- `hibernate-spatial`
- `Docker`, `Docker Compose`
- `MySQL`

## 04. 관련 포스팅

- [공간 인덱스 적용](https://lsh2613.tistory.com/264)

## 05. 시작하기

### 애플리케이션 테스트

**1. 프로젝트 불러오기**

``` bash
  git clone https://github.com/lsh2613/MySQL-Spatial-Idx.git <원하는 경로>
  cd <원하는 경로>
```

**2. 도커 컴포즈 실행**<br>

``` bash
  docker-compose up --build -d
```

**3. 테스트 코드 실행**

``` bash
  ./gradlew clean test --tests "com.spatialidx.SpatialIndexTest"
```

### MySQL 테스트

**1. 테스트 데이터 저장**

``` bash
  ./gradlew test --tests "com.spatialidx.MyCoordinateBatchTest.testSetupData"
```

**2. MySQL 컨테이너 접속**
```bash
  docker exec -it mysql_db bash
```

**3. MySQL 접속**
``` bash
    mysql -u root -p
    Enter password: 1234
```

**4. 데이터베이스 선택**
``` bash
  use spatial_db;
```

**5. EXPLAIN analyze 실행**
> 공간 인덱스를 사용하지 않은 조회 쿼리
> ``` sql
>   EXPLAIN analyze
>   SELECT co.*
>   FROM my_coordinate co IGNORE INDEX (spatial_idx)
>   WHERE ST_Contains(ST_Buffer(ST_GeomFromText('POINT(0 0)', 4326), 5000), co.point)\G
> ```
> ![스크린샷 2025-04-19 오후 7 54 51](https://github.com/user-attachments/assets/24d31599-bab6-4963-a386-1b3208723423)
>
> | 항목                | 값               |
> |-------------------|-----------------|
> | Table scan        | Full Table Scan |
> | 실제 조건에 부합하는 row 수 | 200             |
> | 실제 읽은 row 수       | 10,000          |
> | 전체 데이터 접근 시간      | 2.6 ms          |
> | 조건 평가 시간      | 129 ms          |
> | 실제 소요 시간          | 131.7 ms        |


> 공간 인덱스를 사용한 조회 쿼리
> ``` sql
>   EXPLAIN analyze
>   SELECT co.*
>   FROM my_coordinate co
>   WHERE ST_Contains(ST_Buffer(ST_GeomFromText('POINT(0 0)', 4326), 5000), co.point)\G
> ```
> ![스크린샷 2025-04-19 오후 7 55 17](https://github.com/user-attachments/assets/9c131cff-0de0-487f-b429-7cc7507204ba)
> | 항목                | 값                 |
> |-------------------|-------------------|
> | Table scan        | Index range scan  |
> | 인덱스               | using spatial_idx |
> | 실제 조건에 부합하는 row 수 | 200               |
> | 실제 읽은 row 수       | 200               |
> | 전체 데이터 접근 시간      | 0.659 ms          |
> | 조건 평가 시간      | 7.32 ms           |
> | 전체 소요 시간          | 7.98 ms           |

## 05. 결과

### 애플리케이션에서의 성능 비교
> 대략 1.79배 빠름
<img width="1107" alt="image" src="https://github.com/user-attachments/assets/780b39c2-e0f6-4168-81ba-ad079005c90b">

### MySQL에서의 성능 비교
> | 항목             | 인덱스 사용 시     | 인덱스 사용 안 함   | 차이 (배수)         |
> |------------------|--------------------|---------------------|---------------------|
> | **전체 처리 시간** | 7.98 ms            | 131.7 ms            | **약 16.5배** |
> | **데이터 접근 시간** | 0.659 ms           | 2.6 ms              | **약 3.94배** |
> | **조건 평가 시간** | 7.32 ms            | 129 ms              | **약 17.6배** |
