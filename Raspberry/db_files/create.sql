CREATE TABLE modules_data (
    mod_address varchar(5) not null primary key,
    mod_desc varchar(10) not null
);
INSERT INTO "modules_data" VALUES('0x40','PCA9685');
INSERT INTO "modules_data" VALUES('0x41','PCA9685');
CREATE TABLE legs_data (
    leg_number tinyint primary key not null,
    base_servo tinyint not null,
    femur_servo tinyint not null,
    tarsus_servo tinyint not null
);
INSERT INTO "legs_data" VALUES(1,8,4,1);
INSERT INTO "legs_data" VALUES(2,7,3,0);
INSERT INTO "legs_data" VALUES(3,6,5,2);
INSERT INTO "legs_data" VALUES(4,10,11,9);
INSERT INTO "legs_data" VALUES(5,12,16,13);
INSERT INTO "legs_data" VALUES(6,14,17,15);
CREATE TABLE servos_data (
    mod_address varchar(5) not null,
    local_channel tinyint not null default -1,
    global_channel tinyint not null primary key,
    min float(3,1) not null default 0,
    mid float(3,1) not null default 375,
    max float(3,1) not null default 0,
    limit_min smallint not null default 0,
    limit_max smallint not null default 0,
    FOREIGN KEY (mod_address) references modules_data (mod_address)
);
INSERT INTO "servos_data" VALUES('0x40',1,0,190.0,382.5,575.0,0,0);
INSERT INTO "servos_data" VALUES('0x40',2,1,170.0,380.0,590.0,0,0);
INSERT INTO "servos_data" VALUES('0x40',3,2,185.0,392.5,600.0,0,0);
INSERT INTO "servos_data" VALUES('0x40',4,3,225.0,335.0,445.0,0,0);
INSERT INTO "servos_data" VALUES('0x40',15,4,225.0,335.0,445.0,0,0);
INSERT INTO "servos_data" VALUES('0x40',6,5,250.0,355.0,460.0,0,0);
INSERT INTO "servos_data" VALUES('0x40',7,6,255.0,365.0,475.0,0,0);
INSERT INTO "servos_data" VALUES('0x40',8,7,250.0,360.0,470.0,0,0);
INSERT INTO "servos_data" VALUES('0x40',9,8,275.0,375.0,475.0,0,0);
INSERT INTO "servos_data" VALUES('0x41',1,9,160.0,380.0,600.0,0,0);
INSERT INTO "servos_data" VALUES('0x41',2,10,255.0,365.0,475.0,0,0);
INSERT INTO "servos_data" VALUES('0x41',3,11,215.0,325.0,435.0,0,0);
INSERT INTO "servos_data" VALUES('0x41',4,12,265.0,370.0,475.0,0,0);
INSERT INTO "servos_data" VALUES('0x41',5,13,165.0,377.5,590.0,0,0);
INSERT INTO "servos_data" VALUES('0x41',6,14,255.0,377.5,500.0,0,0);
INSERT INTO "servos_data" VALUES('0x41',7,15,152.5,365.0,577.5,0,0);
INSERT INTO "servos_data" VALUES('0x41',8,16,210.0,325.0,440.0,0,0);
INSERT INTO "servos_data" VALUES('0x41',9,17,215.0,330.0,445.0,0,0);
