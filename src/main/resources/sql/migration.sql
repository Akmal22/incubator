--liquibase formatted sql

--changeset rakhimbaev:changeset-0
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'ROLES';
CREATE TABLE ROLES
(
    ID   NUMERIC(22, 0) PRIMARY KEY,
    NAME VARCHAR(32) NOT NULL UNIQUE
);
CREATE SEQUENCE SEQ_ROLES START WITH 1;
--rollback not required

--changeset rakhimbaev:changeset-1
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from roles where upper(name) = 'ADMIN';
--precondition-sql-check expectedResult:0 select count(*) from roles where upper(name) = 'BI_MANAGER';
--precondition-sql-check expectedResult:0 select count(*) from roles where upper(name) = 'USER';
INSERT INTO ROLES(ID, NAME) VALUES (nextval('SEQ_ROLES'), 'ROLE_ADMIN');
INSERT INTO ROLES(ID, NAME) VALUES (nextval('SEQ_ROLES'), 'ROLE_BI_MANAGER');
INSERT INTO ROLES(ID, NAME) VALUES (nextval('SEQ_ROLES'), 'ROLE_USER');
--rollback not required


--changeset rakhimbaev:changeset-2
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'USERS';
CREATE TABLE USERS
(
    ID       NUMERIC(22, 0) PRIMARY KEY,
    UUID     VARCHAR(64)  NOT NULL UNIQUE,
    USERNAME VARCHAR(64)  NOT NULL,
    EMAIL    VARCHAR(64)  NOT NULL UNIQUE,
    PASSWORD VARCHAR(128) NOT NULL,
    ROLE_ID  NUMERIC(22, 0) REFERENCES ROLES (ID)
);
CREATE SEQUENCE SEQ_USERS START WITH 1;
--rollback not required

--changeset rakhimbaev:changeset-3-fix
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from users where username='admin';
INSERT INTO USERS(ID, UUID, USERNAME, EMAIL, PASSWORD, ROLE_ID) VALUES (nextval('seq_users'), '00e384f5-2179-4b43-89db-aaad5f67040f', 'admin', 'akmal9433@gmail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', (select id from roles where name = 'ADMIN'));
--rollback not required

--changeset rakhimbaev:changeset-4
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from users where username='user';
INSERT INTO USERS(ID, UUID, USERNAME, EMAIL, PASSWORD, ROLE_ID) VALUES (nextval('seq_users'), 'dd502d8b-1222-4f19-8ac6-b71ae85425a8', 'user', 'mail@mail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', (select id from roles where name = 'USER'));
--rollback not required


