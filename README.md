### 01. 프로젝트 설명
- Spring + MySQL의 Spatial Index를 적용한 개인 프로젝트
- 자세한 설명과 테스트는 [블로그](https://lsh2613.tistory.com/264)를 통해 확인해볼 수 있다

### 02. 기능
- Spring에서 MySQL의 공간 데이터 적용 및 생성
- Spring에서 MySQL의 공간 인덱스를 활용한 조회
- 공간 인덱스를 타지 않은 조회, 공간 인덱스 조회의 성능 비교

### 03. 사용 기술
- `Spring Boot 3.2`, `Spring Data JPA`
- `hibernate-spatial`
- `Docker`, `Docker Compose`
- `MySQL`

### 04. 프로젝트 설명
- MySQL의 공간 데이터와 공간 인덱스를 활용하기 위한 실습 프로젝트로 별도의 API는 존재하지 않는다
- MySQL의 공간 데이터를 생성하는 테스트 코드 구현
- MySQL의 공간 인덱스를 활용한 조회 테스트 코드 구현
- 10,000개의 테스트 데이터를 위한 bulk insert query를 활용

### 05. 공간 인덱스 성능 비교
> 총 10,000 개의 데이터 중 특정 Point 기준으로 반경 5000m 이내의 200개의 Point를 공간 인덱스를 통한 조회와, 공간 인덱스를 타지 않은 조회를 비교한다

**애플리케이션에서의 성능 비교**
> 대략 1.79배 빠름
<img width="1107" alt="image" src="https://github.com/user-attachments/assets/780b39c2-e0f6-4168-81ba-ad079005c90b">

**MySQL에서의 성능 비교**
> 대략 22배 빠름
<img width="1107" alt="image" src="https://github.com/user-attachments/assets/5b51a99b-8a6e-4bfd-be2a-ff0d76b1d015">


### 06. 시작하기
**1. 도커 컴포즈를 통해 MySQL 띄우기**<br>
docker-compose.yml이 존재하는 루트 디렉토리로 이동
``` shell
docker-compose up
```

**2. 테스트 쿼리 성능 비교 확인**
- 방법 1. 애플리케이션 레벨에서 성능 확인
  1. @Test void 공간_인덱스_조회(), @Test void 공간_인덱스_없이_조회()를 각각 따로 실행
  2. 실행 시간 확인
- 방법 2. MySQL 프로파일링을 통한 확인
  - ``` sql
    SET profiling = 1;

    SELECT co.*
    FROM my_coordinate co IGNORE INDEX (spatial_idx)
    WHERE ST_Contains(ST_Buffer(ST_GeomFromText('POINT(0 0)', 4326), 5000), co.point);

    SELECT co.*
    FROM my_coordinate co
    WHERE ST_Contains(ST_Buffer(ST_GeomFromText('POINT(0 0)', 4326), 5000), co.point);
    
    show profiles;
    ```
- 방법 3. MySQL의 Explain analyze를 통한 확인
    - ``` sql
      EXPLAIN analyze
      SELECT co.*
      FROM my_coordinate co IGNORE INDEX (spatial_idx)
      WHERE ST_Contains(ST_Buffer(ST_GeomFromText('POINT(0 0)', 4326), 5000), co.point);
      
      EXPLAIN analyze
      SELECT co.*
      FROM my_coordinate co
      WHERE ST_Contains(ST_Buffer(ST_GeomFromText('POINT(0 0)', 4326), 5000), co.point);
      ```
