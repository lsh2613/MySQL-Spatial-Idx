ALTER TABLE my_coordinate MODIFY COLUMN point POINT NOT NULL SRID 4326;

CREATE SPATIAL INDEX spatial_idx ON my_coordinate (point);
