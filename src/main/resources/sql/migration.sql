--liquibase formatted sql

--changeset rakhimbaev:changeset-0
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'USERS';
CREATE TABLE USERS
(
    ID       NUMERIC(22, 0) PRIMARY KEY,
    UUID     VARCHAR(64)  NOT NULL UNIQUE,
    USERNAME VARCHAR(64)  NOT NULL UNIQUE,
    EMAIL    VARCHAR(64)  NOT NULL,
    PASSWORD VARCHAR(128) NOT NULL,
    ROLE  VARCHAR(64) NOT NULL
);
CREATE SEQUENCE SEQ_USERS START WITH 1;
--rollback not required

--changeset rakhimbaev:changeset-1
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from users where username='admin';
INSERT INTO USERS(ID, UUID, USERNAME, EMAIL, PASSWORD, ROLE) VALUES (nextval('seq_users'), '00e384f5-2179-4b43-89db-aaad5f67040f', 'admin', 'akmal9433@gmail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_ADMIN');
INSERT INTO USERS(ID, UUID, USERNAME, EMAIL, PASSWORD, ROLE) VALUES (nextval('seq_users'), 'dd502d8b-1222-4f19-8ac6-b71ae85425a8', 'user', 'user@mail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_BI_MANAGER');
INSERT INTO USERS(ID, UUID, USERNAME, EMAIL, PASSWORD, ROLE) VALUES (nextval('seq_users'), 'e5f02d24-bd70-4ef1-9719-df5ab21f51d8', 'bi-manager', 'manager@mail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_USER');
--rollback not required