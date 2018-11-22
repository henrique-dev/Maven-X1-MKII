CREATE TABLE modules_data (
    mod_address varchar(5) not null primary key,
    mod_desc varchar(10) not null
);

INSERT INTO "modules_data" VALUES('0x40','PCA9685');
INSERT INTO "modules_data" VALUES('0x41','PCA9685');


CREATE TABLE legs_data (
    leg_number tinyint primary key not null,
    base_servo tinyint not null,
    base_length float not null default 0,
    femur_servo tinyint not null,
    femur_length float not null default 0,
    tarsus_servo tinyint not null,
    tarsus_length float not null default 0);

INSERT INTO "legs_data" VALUES(1,8,4,1,23.522,113.422,147.427);
INSERT INTO "legs_data" VALUES(2,7,3,0,23.522,113.422,147.427);
INSERT INTO "legs_data" VALUES(3,10,11,9,41.532,113.422,147.427);
INSERT INTO "legs_data" VALUES(4,6,5,2,41.532,113.422,147.427);
INSERT INTO "legs_data" VALUES(5,12,16,13,23.522,113.422,147.427);
INSERT INTO "legs_data" VALUES(6,14,17,15,23.522,113.422,147.427);


CREATE TABLE servos_data (
    mod_address varchar(5) not null,
    local_channel tinyint not null default -1,
    global_channel tinyint not null primary key,
    min float(3,1) not null default 0,
    mid float(3,1) not null default 375,
    max float(3,1) not null default 0,
    limit_min smallint not null default 0,
    limit_max smallint not null default 0,
    degrees_opening smallint not null default 0,
    inverted boolean not null default false,
    FOREIGN KEY (mod_address) references modules_data (mod_address)
);

INSERT INTO "servos_data" VALUES('0x40',1,0,172.5,364.5,557.5,-50,40,180,'false');
INSERT INTO "servos_data" VALUES('0x40',2,1,180.0,390.0,600.0,-50,40,180,1);
INSERT INTO "servos_data" VALUES('0x40',3,2,185.0,392.5,600.0,-50,40,180,1);
INSERT INTO "servos_data" VALUES('0x40',4,3,225.0,335.0,445.0,-45,45,90,1);
INSERT INTO "servos_data" VALUES('0x40',15,4,225.0,335.0,445.0,-45,45,90,'false');
INSERT INTO "servos_data" VALUES('0x40',6,5,250.0,355.0,460.0,-45,45,90,'false');
INSERT INTO "servos_data" VALUES('0x40',7,6,255.0,365.0,475.0,-45,45,90,'false');
INSERT INTO "servos_data" VALUES('0x40',8,7,250.0,360.0,470.0,-45,45,90,1);
INSERT INTO "servos_data" VALUES('0x40',9,8,275.0,375.0,475.0,-45,45,90,'false');
INSERT INTO "servos_data" VALUES('0x41',1,9,160.0,380.0,600.0,-50,40,180,'false');
INSERT INTO "servos_data" VALUES('0x41',2,10,255.0,365.0,475.0,-45,45,90,1);
INSERT INTO "servos_data" VALUES('0x41',3,11,215.0,325.0,435.0,-45,45,90,1);
INSERT INTO "servos_data" VALUES('0x41',4,12,265.0,370.0,475.0,-45,45,90,'false');
INSERT INTO "servos_data" VALUES('0x41',5,13,165.0,377.5,590.0,-50,40,180,'false');
INSERT INTO "servos_data" VALUES('0x41',6,14,255.0,377.5,500.0,-45,45,90,1);
INSERT INTO "servos_data" VALUES('0x41',7,15,162.5,375.0,587.5,-50,40,180,1);
INSERT INTO "servos_data" VALUES('0x41',8,16,210.0,325.0,440.0,-45,45,90,1);
INSERT INTO "servos_data" VALUES('0x41',9,17,215.0,330.0,445.0,-45,45,90,'false');


CREATE TABLE robot_data (
    body_length float not null,
    body_width float not null, body_height float not null default 0);

INSERT INTO "robot_data" VALUES(220.15,121.687,50.093);
