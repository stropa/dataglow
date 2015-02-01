DROP TABLE IF EXISTS merchants;

CREATE TABLE merchants (
  id integer NOT NULL primary key,
  merchant_name varchar(255),
  region varchar(50) default NULL,
  business_type varchar(255) default NULL
);

INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (1,'Sem Semper Erat Industries','Maharastra','sports'),(2,'Sem Inc.','Paraná','banking'),(3,'Donec Sollicitudin Adipiscing Institute','MG','agriculture'),(4,'Fusce Diam Ltd','PIE','agriculture'),(5,'Sit Amet Corp.','BU','agriculture'),(6,'Risus Duis A LLC','KP',''),(7,'Duis Mi Enim Corp.','','services'),(8,'Adipiscing Industries','','food'),(9,'Nulla LLP','Comunitat Valenciana','entertainment'),(10,'Fusce Incorporated','','food');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (11,'Nec Mauris Blandit LLP','HB','agriculture'),(12,'Ligula Eu Enim LLP','KD','sports'),(13,'Mauris Non Dui Incorporated','BE','services'),(14,'Pellentesque Ultricies Associates','South Island','agriculture'),(15,'Neque Foundation','Katsina','sports'),(16,'Rutrum Consulting','M','it'),(17,'Nulla Inc.','',''),(18,'Vehicula Company','ME','finances'),(19,'Vel Arcu Eu Limited','GA','banking'),(20,'Rhoncus Nullam Velit LLC','Katsina','sports');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (21,'Ornare LLC','SK','agriculture'),(22,'Ac Turpis Limited','Luxemburg','cust.electronics'),(23,'Tortor Limited','Nova Scotia','banking'),(24,'Non Luctus Sit Company','N.','consulting'),(25,'Tellus PC','Luxemburg','it'),(26,'Diam At Ltd','Zl','services'),(27,'Tellus Industries','C','sports'),(28,'Lorem Auctor Quis Incorporated','Piemonte','sports'),(29,'Laoreet Inc.','NSW','agriculture'),(30,'Magnis Dis Parturient Consulting','NO','banking');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (31,'Ipsum Foundation','North Rhine-Westphalia','services'),(32,'Nisl LLC','Victoria',''),(33,'Lectus Incorporated','','food'),(34,'Gravida Company','MP',''),(35,'Sem Consequat Nec Foundation','','sports'),(36,'Vestibulum Consulting','AN','food'),(37,'Eleifend Consulting','HH','finances'),(38,'Interdum Ligula Institute','ON','entertainment'),(39,'Luctus Ut Pellentesque Company','NSW','services'),(40,'Nascetur Limited','Paraíba','cust.electronics');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (41,'Nulla Corp.','Lazio','food'),(42,'Enim Institute','TN','food'),(43,'Donec Felis Orci Industries','Île-de-France','services'),(44,'Metus Limited','Ontario','sports'),(45,'Erat Vitae Risus Corporation','PR','cust.electronics'),(46,'Non Nisi Aenean Company','MA','food'),(47,'Duis Company','CO','food'),(48,'Sed LLP','VDA','food'),(49,'Pellentesque Habitant Corp.','Perthshire','consulting'),(50,'Et Incorporated','SJ','food');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (51,'At Inc.','NI','cust.electronics'),(52,'Fusce Aliquet Magna Inc.','NSW','finances'),(53,'Phasellus Consulting','SP','sports'),(54,'Quis Pede Limited','SJ','banking'),(55,'Natoque Penatibus Et LLC','P','cust.electronics'),(56,'Blandit Corporation','Gelderland','agriculture'),(57,'Augue Inc.','Clackmannanshire','food'),(58,'Ridiculus Mus Institute','Abruzzo','banking'),(59,'Metus Ltd','Luxemburg','banking'),(60,'Orci Luctus Et Corporation','KL','food');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (61,'Tellus Corporation','WL','banking'),(62,'Tempus Corporation','ERM','consulting'),(63,'Arcu Consulting','U','sports'),(64,'Et Magnis LLC','Languedoc-Roussillon','cust.electronics'),(65,'Ipsum Incorporated','Victoria',''),(66,'Nonummy Ut Corp.','Lombardia','sports'),(67,'Tristique Senectus Et Corp.','L','consulting'),(68,'Dictum Augue Corp.','Fr','consulting'),(69,'Sem Pellentesque Corporation','Munster','services'),(70,'In Faucibus Orci Associates','Ontario','banking');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (71,'Eget Corp.','AB','agriculture'),(72,'Tincidunt PC','New South Wales',''),(73,'Enim Consulting','Schleswig-Holstein','sports'),(74,'Feugiat Tellus Industries','Vlaams-Brabant','it'),(75,'Quis LLC','',''),(76,'Eu Augue Ltd','Benue','consulting'),(77,'Hymenaeos Mauris Associates','WB','entertainment'),(78,'Quisque Purus Ltd','','consulting'),(79,'Posuere Ltd','QLD','cust.electronics'),(80,'Dolor Dolor Consulting','Swiętokrzyskie','sports');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (81,'Quam Dignissim Pharetra LLP','Pays de la Loire','it'),(82,'Dui Lectus Rutrum PC','SS','it'),(83,'Lorem Donec Elementum Consulting','NL','banking'),(84,'At Lacus Quisque Limited','Zl','food'),(85,'Cursus Diam At Incorporated','Dorset','cust.electronics'),(86,'Sapien Cras Institute','Ceará','finances'),(87,'Ridiculus Mus Aenean Ltd','L','agriculture'),(88,'Tincidunt Congue Corp.','KS','food'),(89,'Magna Company','Navarra','entertainment'),(90,'Consequat Enim Diam Incorporated','Rivers','food');
INSERT INTO merchants (id,merchant_name,region,business_type) VALUES (91,'Nisl Quisque Foundation','PO','agriculture'),(92,'Molestie Sodales Associates','CO','cust.electronics'),(93,'Nibh Vulputate Mauris Corp.','Umbria','sports'),(94,'Ac Turpis Inc.','Z.','it'),(95,'Dolor Inc.','IN','agriculture'),(96,'Placerat Orci Lacus Ltd','Rio de Janeiro','food'),(97,'Nec Quam Curabitur Inc.','Canarias','it'),(98,'Tincidunt Consulting','HB',''),(99,'Amet Limited','Odisha','agriculture'),(100,'Et Ltd','AN','entertainment');


DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
  merchant_id integer NULL,
  amount integer,
  order_id varchar(255),
  currency varchar(255) default NULL,
  description varchar(255) default NULL,
  state varchar(255) default NULL,
  cardholder varchar(255) default NULL,
  ip varchar(255),
  registered_time timestamp,
  pan varchar(255),
  id integer not null primary key
);



--alter table "orders" alter column amount type bigint using (amount::float);
--update "orders" set amount = amount * 100;

--alter table orders add reg_time timestamp with time zone;
--update orders set reg_time = to_timestamp(registered_time::integer);