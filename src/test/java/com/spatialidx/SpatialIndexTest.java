package com.spatialidx;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class SpatialIndexTest {

    @Autowired
    private MyCoordinateRepository repository;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    @BeforeAll
    static void setup(@Autowired MyCoordinateRepository repository) {
        if (repository.count() == 0) {
            // 중심 좌표 (0, 0)에서 반경 5000m 내에 포함될 좌표 200개 생성
            for (int i = 0; i < 200; i++) {
                Point point = geometryFactory.createPoint(new Coordinate(
                        Math.random() * 0.01 - 0.005, // -0.005 ~ 0.005 범위의 위도
                        Math.random() * 0.01 - 0.005  // -0.005 ~ 0.005 범위의 경도
                ));
                repository.save(MyCoordinate.createMyCoordinateWithSRID4326(point));
            }

            // 중심 좌표 (0, 0)에서 반경 5000m 내에 포함될 좌표 300개 생성
            for (int i = 0; i < 300; i++) {
                Point point = geometryFactory.createPoint(new Coordinate(
                        Math.random() * 180 - 90, // 임의의 위도 (-90 ~ 90)
                        Math.random() * 180 - 90 // 임의의 경도 (-180 ~ 180)
                ));
                repository.save(MyCoordinate.createMyCoordinateWithSRID4326(point));
            }
        }
    }

    @Test
    void testFindAllWithInCircleArea() {
        //given
        Point center = geometryFactory.createPoint(new Coordinate(0, 0));
        center.setSRID(4326);
        int radius = 5000;

        //when
        List<MyCoordinate> results = repository.findAllWithInCircleArea(center, radius);

        // 결과 검증
        assertThat(results.size()).isGreaterThanOrEqualTo(200);
    }
}

