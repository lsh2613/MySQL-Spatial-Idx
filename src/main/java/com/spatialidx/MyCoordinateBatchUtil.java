package com.spatialidx;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MyCoordinateBatchUtil {

    private final JdbcTemplate jdbcTemplate;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public void setupData(int totalDataCnt, int queryHit) {
        List<MyCoordinate> myCoordinates = new ArrayList<>();

        myCoordinates.addAll(generateCoordinatesWithin5000m(queryHit));
        myCoordinates.addAll(generateRandomCoordinates(totalDataCnt - queryHit));

        batchInsertCoordinates(jdbcTemplate, myCoordinates);
    }

    private static List<MyCoordinate> generateCoordinatesWithin5000m(int cnt) {
        List<MyCoordinate> myCoordinates = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            Point point = geometryFactory.createPoint(new Coordinate(
                    Math.random() * 0.01 - 0.005, // -0.005 ~ 0.005 범위의 위도
                    Math.random() * 0.01 - 0.005  // -0.005 ~ 0.005 범위의 경도
            ));
            myCoordinates.add(MyCoordinate.createWithSRID4326(point));
        }
        return myCoordinates;
    }

    private static List<MyCoordinate> generateRandomCoordinates(int cnt) {
        List<MyCoordinate> myCoordinates = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            Point point = geometryFactory.createPoint(new Coordinate(
                    Math.random() * 180 - 90, // 임의의 위도 (-90 ~ 90)
                    Math.random() * 180 - 90 // 임의의 경도 (-180 ~ 180)
            ));
            myCoordinates.add(MyCoordinate.createWithSRID4326(point));
        }
        return myCoordinates;
    }

    private static void batchInsertCoordinates(JdbcTemplate jdbcTemplate, List<MyCoordinate> coordinates) {
        String sql = "INSERT INTO my_coordinate (point) VALUES (ST_PointFromText(?, 4326))";

        List<Object[]> batchArgs = new ArrayList<>();
        for (MyCoordinate coordinate : coordinates) {
            batchArgs.add(new Object[]{coordinate.getPoint().toText()});
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }


    public void cleanupData() {
        jdbcTemplate.execute("DELETE FROM my_coordinate");
    }

}
