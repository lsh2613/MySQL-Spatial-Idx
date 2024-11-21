### 01. 프로젝트 설명
- Spring + MySQL의 Spatial Index를 적용한 개인 프로젝트
- 자세한 설명과 테스트는 [블로그](https://lsh2613.tistory.com/264)를 통해 확인해볼 수 있다

### 02. 기능
- MySQL의 공간 데이터 생성
- MySQL의 공간 인덱스를 활용한 조회
- 공간 인덱스를 타지 않은 조회, 공간 인덱스 조회의 성능 비교

### 03. 사용 기술
- `Spring Boot 3.2`, `Spring Data JPA`
- `Docker`, `Docker Compose`
- `MySQL`

### 04. 프로젝트 설명
- MySQL의 공간 데이터와 공간 인덱스를 활용하기 위한 실습 프로젝트로 별도의 API는 존재하지 않는다
- MySQL의 공간 데이터를 생성하는 테스트 코드 구현
- MySQL의 공간 인덱스를 활용한 조회 테스트 코드 구현
- 테스트 데이터를 위한 bulk insert query를 활용

### 05. 시작하기
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
