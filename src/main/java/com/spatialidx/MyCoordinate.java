package com.spatialidx;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class MyCoordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,
            columnDefinition = "POINT NOT NULL SRID 4326")
    private Point point;

    private MyCoordinate(Point point) {
        this.point = point;
    }

    static public MyCoordinate createWithSRID4326(Point point) {
        point.setSRID(4326);
        return new MyCoordinate(point);
    }
}
