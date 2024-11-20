package com.spatialidx;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MyCoordinateTest {

    @Autowired
    private MyCoordinateRepository myCoordinateRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    void createPointWithFactory() {
        //given
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(180, 90);

        //when
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326);

        //then
        assertThat(point.getX()).isEqualTo(180);
        assertThat(point.getY()).isEqualTo(90);
    }

    @Test
    void createPointWithWKT() throws ParseException {
        //given
        WKTReader wktReader = new WKTReader();

        //when
        Point point = (Point) wktReader.read("POINT(180 90)");
        point.setSRID(4326);

        //then
        assertThat(point.getX()).isEqualTo(180);
        assertThat(point.getY()).isEqualTo(90);
    }

    @Test
    void selectWithSpatialException() {
        //given
        Point point = geometryFactory.createPoint(new Coordinate(10, 110));
        point.setSRID(4326);
        MyCoordinate myCoordinate = myCoordinateRepository.save(MyCoordinate.createMyCoordinateWithSRID4326(point));

        //when
        MyCoordinate saved = myCoordinateRepository.findById(myCoordinate.getId()).get();

        //then
        assertThat(saved.getPoint().getX()).isEqualTo(10);
        assertThat(saved.getPoint().getY()).isEqualTo(110);
    }

    @Test
    void createPolygonWithFactory() {
        //given
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(10, 0),
                new Coordinate(10, 10),
                new Coordinate(0, 10),
                new Coordinate(0, 0),
        };

        //when
        Polygon polygon = geometryFactory.createPolygon(coordinates);
        polygon.setSRID(4326); // SRID 설정

        //then
        assertThat(polygon.getCoordinates()).hasSize(5);
    }

    @Test
    void createPolygonWithWKT() throws ParseException {
        //given
        WKTReader wktReader = new WKTReader();
        Geometry read = wktReader.read("POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))");

        //when
        Polygon polygon = (Polygon) read;
        polygon.setSRID(4326);

        //then
        assertThat(polygon.getCoordinates()).hasSize(5);
    }

    @Test
    void findAllWithInCircle() {
        //given
        Point point = geometryFactory.createPoint(new Coordinate(20, 10));
        point.setSRID(4326);
        MyCoordinate ce1 = myCoordinateRepository.save(MyCoordinate.createMyCoordinateWithSRID4326(point));

        Point point2 = geometryFactory.createPoint(new Coordinate(40, 40));
        point2.setSRID(4326);
        MyCoordinate ce2 = MyCoordinate.createMyCoordinateWithSRID4326(point2);
        myCoordinateRepository.save(ce2);

        //when
        List<MyCoordinate> allContainArea = myCoordinateRepository.findAllWithInCircleArea(point, 5000);

        //then
        assertThat(allContainArea).hasSize(1);
        assertThat(allContainArea.get(0)).isEqualTo(ce1);
        assertThat(allContainArea.get(0).getPoint().getX()).isEqualTo(20);
        assertThat(allContainArea.get(0).getPoint().getY()).isEqualTo(10);
    }

}
