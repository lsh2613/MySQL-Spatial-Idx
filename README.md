### 01. 프로젝트 설명
- Spring + MySQL의 Spatial Index를 적용한 개인 프로젝트
- 자세한 설명과 테스트는 [블로그]([https://lsh2613.tistory.com/260](https://lsh2613.tistory.com/264))를 통해 확인해볼 수 있다

### 02. 기능
- MySQL의 공간 데이터 생성
- MySQL의 공간 인덱스를 활용한 조회

### 03. 사용 기술
- `Spring Boot 3.2`, `Spring Data JPA`
- `Docker`, `Docker Compose`
- `MySQL`

### 04. 프로젝트 설명
- MySQL의 공간 데이터와 공간 인덱스를 활용하기 위한 실습 프로젝트로 별도의 API는 존재하지 않는다
- MySQL의 공간 데이터를 생성하는 테스트 코드 구현
- MySQL의 공간 인덱스를 활용한 조회 테스트 코드 구현
- 목 데이터를 위한 bulk insert query를 활용

### 05. 시작하기
**1. 도커 컴포즈를 통해 MySQL 띄우기**
docker-compose.yml이 존재하는 루트 디렉토리로 이동
``` shell
docker-compose up
```

**2. 테스트 코드 실행 확인**
- 만약 Explain을 통해 인덱스가 타는지 확인을 하려면
    1. SpatialIndexTest.testFindAllWithInCircleArea()에 @Rollback(false) 추가
    2. Explain 쿼리 실행
       ``` sql
       EXPLAIN
       SELECT co.id
       FROM my_coordinate co
       WHERE ST_Contains(ST_Buffer(ST_GeomFromText('POINT(0 0)', 4326), 5000), co.point);
       ```
    3. 결과 확인

       ![image](https://github.com/user-attachments/assets/d86c3e75-009c-4b1e-9cd7-e20ce07b39ea)
