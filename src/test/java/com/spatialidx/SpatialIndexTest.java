package com.spatialidx;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class SpatialIndexTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MyCoordinateRepository repository;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    void setupData() {
        // 중심 좌표 (0, 0)에서 반경 5000m 내에 포함될 좌표 200개 생성
        List<MyCoordinate> myCoordinates = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Point point = geometryFactory.createPoint(new Coordinate(
                    Math.random() * 0.01 - 0.005, // -0.005 ~ 0.005 범위의 위도
                    Math.random() * 0.01 - 0.005  // -0.005 ~ 0.005 범위의 경도
            ));
            myCoordinates.add(MyCoordinate.createMyCoordinateWithSRID4326(point));
        }

        // 중심 좌표 (0, 0)에서 반경 5000m 내에 포함되지 않는 좌표 300개 생성
        for (int i = 0; i < 300; i++) {
            Point point = geometryFactory.createPoint(new Coordinate(
                    Math.random() * 180 - 90, // 임의의 위도 (-90 ~ 90)
                    Math.random() * 180 - 90 // 임의의 경도 (-180 ~ 180)
            ));
            myCoordinates.add(MyCoordinate.createMyCoordinateWithSRID4326(point));
        }

        batchInsertCoordinates(myCoordinates);
    }

    private void batchInsertCoordinates(List<MyCoordinate> coordinates) {
        String sql = "INSERT INTO my_coordinate (point) VALUES (ST_PointFromText(?, 4326))";

        List<Object[]> batchArgs = new ArrayList<>();
        for (MyCoordinate coordinate : coordinates) {
            batchArgs.add(new Object[] { coordinate.getPoint().toText() });
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Test
    void testFindAllWithInCircleArea() {
        //given
        setupData();

        Point center = geometryFactory.createPoint(new Coordinate(0, 0));
        center.setSRID(4326);
        int radius = 5000;

        //when
        List<MyCoordinate> results = repository.findAllWithInCircleArea(center, radius);

        // 결과 검증
        assertThat(results.size()).isGreaterThanOrEqualTo(200);
    }
}
