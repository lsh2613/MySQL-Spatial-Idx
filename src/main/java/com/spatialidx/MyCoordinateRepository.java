package com.spatialidx;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyCoordinateRepository extends JpaRepository<MyCoordinate, Long> {

    @Query("""  
        SELECT co  
        FROM MyCoordinate AS co  
        WHERE st_contains(st_buffer(:center, :radius), co.point)  
        """)
    List<MyCoordinate> findAllWithInCircleArea(@Param("center") final Point center,
                                               @Param("radius") final int radius);

}
