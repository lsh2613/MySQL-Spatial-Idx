package com.spatialidx;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SpatialIndexTest {

    @Autowired
    private MyCoordinateRepository repository;

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static final int QUERY_HIT = 200;

    @BeforeAll
    static void setupData(@Autowired MyCoordinateBatchUtil myCoordinateBatchUtil) {
        final int totalDataCnt = 10_000;
        myCoordinateBatchUtil.setupData(totalDataCnt, QUERY_HIT);
    }

    @AfterAll
    static void cleanupData(@Autowired MyCoordinateBatchUtil myCoordinateBatchUtil) {
        myCoordinateBatchUtil.cleanupData();
    }

    @Test
    void 공간_인덱스_조회() {
        //given
        Point center = geometryFactory.createPoint(new Coordinate(0, 0));
        center.setSRID(4326);
        int radius = 5000;

        //when
        StopWatch stopwatch = new StopWatch("공간 인덱스");

        stopwatch.start("공간 인덱스를 통한 조회");
        List<MyCoordinate> results = repository.findAllWithInCircleAreaWithIdx(center, radius);
        stopwatch.stop();

        System.out.println(stopwatch.prettyPrint());

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
        StopWatch stopwatch = new StopWatch("공간 인덱스 X");

        stopwatch.start("공간 인덱스 없이 조회");
        List<MyCoordinate> results = repository.findAllWithInCircleAreaWithoutIdx(center, radius);
        stopwatch.stop();

        System.out.println(stopwatch.prettyPrint());

        //then
        assertThat(results.size()).isGreaterThanOrEqualTo(QUERY_HIT);
    }
}
