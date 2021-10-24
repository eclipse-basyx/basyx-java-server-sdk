CREATE DATABASE "basyx-map";

\c basyx-map

CREATE SCHEMA elements;

CREATE DATABASE "basyx-directory";

\c basyx-directory

CREATE SCHEMA directory;

CREATE TABLE directory.directory ( "ElementRef" varchar, "ElementID" varchar primary key );

INSERT INTO directory.directory ( "ElementID", "ElementRef" ) VALUES ( 'urn:de.FHG:es.iese:aas:0.98:5:lab/microscope#A-19', 'content.aas1' );

INSERT INTO directory.directory ( "ElementID", "ElementRef" ) VALUES ( 'urn:de.FHG:es.iese:aas:0.98:5:lab/microscope#A-18', 'content.aas2' );

INSERT INTO directory.directory ( "ElementID", "ElementRef" ) VALUES ( 'urn:de.FHG:es.iese:aas:0.98:5:lab/microscope#A-17', 'content.aas3' );

INSERT INTO directory.directory ( "ElementID", "ElementRef" ) VALUES ( 'urn:de.FHG:es.iese:aas:0.98:5:lab/microscope#A-16', 'content.aas4' );

INSERT INTO directory.directory ( "ElementID", "ElementRef" ) VALUES ( 'urn:de.FHG:es.iese:aas:0.98:5:lab/microscope#A-15', 'content.aas5' ); 
