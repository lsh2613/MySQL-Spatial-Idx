package com.spatialidx;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(false) // Explain 활용을 위한 목데이터 유지 시 주석 제거
@Transactional
@SpringBootTest
public class SpatialIndexTest {

    @Autowired
    private MyCoordinateRepository repository;

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static final int QUERY_HIT = 200;

    @BeforeAll
    static void setupData(@Autowired JdbcTemplate jdbcTemplate) {
        // 중심 좌표 (0, 0)에서 반경 5000m 내에 포함될 좌표 QUERY_HIT개 생성
        List<MyCoordinate> myCoordinates = new ArrayList<>();

        for (int i = 0; i < QUERY_HIT; i++) {
            Point point = geometryFactory.createPoint(new Coordinate(
                    Math.random() * 0.01 - 0.005, // -0.005 ~ 0.005 범위의 위도
                    Math.random() * 0.01 - 0.005  // -0.005 ~ 0.005 범위의 경도
            ));
            myCoordinates.add(MyCoordinate.createMyCoordinateWithSRID4326(point));
        }

        // 중심 좌표 (0, 0)에서 반경 5000m 내에 포함되지 않는 좌표 나머지 생성
        for (int i = 0; i < 10000 - QUERY_HIT; i++) {
            Point point = geometryFactory.createPoint(new Coordinate(
                    Math.random() * 180 - 90, // 임의의 위도 (-90 ~ 90)
                    Math.random() * 180 - 90 // 임의의 경도 (-180 ~ 180)
            ));
            myCoordinates.add(MyCoordinate.createMyCoordinateWithSRID4326(point));
        }

        batchInsertCoordinates(jdbcTemplate, myCoordinates);
    }

    private static void batchInsertCoordinates(JdbcTemplate jdbcTemplate, List<MyCoordinate> coordinates) {
        String sql = "INSERT INTO my_coordinate (point) VALUES (ST_PointFromText(?, 4326))";

        List<Object[]> batchArgs = new ArrayList<>();
        for (MyCoordinate coordinate : coordinates) {
            batchArgs.add(new Object[] { coordinate.getPoint().toText() });
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Test
    void 공간_인덱스_조회() {
        //given
        Point center = geometryFactory.createPoint(new Coordinate(0, 0));
        center.setSRID(4326);
        int radius = 5000;

        //when
        long start = System.currentTimeMillis();
        List<MyCoordinate> results = repository.findAllWithInCircleAreaWithIdx(center, radius);
        long end = System.currentTimeMillis();
        System.out.println("공간 인덱스 조회 실행 시간 = " + (end-start));

        //then
        assertThat(results.size()).isGreaterThanOrEqualTo(QUERY_HIT);
    }

    @Test
    void 공간_인덱스_없이_조회() {
        //given
        Point center = geometryFactory.createPoint(new Coordinate(0, 0));
        center.setSRID(4326);
        int radius = 5000;

        //when
        long start = System.currentTimeMillis();
        List<MyCoordinate> results = repository.findAllWithInCircleAreaWithoutIdx(center, radius);
        long end = System.currentTimeMillis();
        System.out.println("공간 인덱스 없이 조회 실행 시간 = " + (end-start));

        //then
        assertThat(results.size()).isGreaterThanOrEqualTo(QUERY_HIT);
    }
}
