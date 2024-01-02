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
INSERT INTO USERS(ID, UUID, USERNAME, EMAIL, PASSWORD, ROLE) VALUES (nextval('seq_users'), 'dd502d8b-1222-4f19-8ac6-b71ae85425a8', 'user', 'user@mail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_USER');
INSERT INTO USERS(ID, UUID, USERNAME, EMAIL, PASSWORD, ROLE) VALUES (nextval('seq_users'), 'e5f02d24-bd70-4ef1-9719-df5ab21f51d8', 'bi-manager', 'manager@mail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_BI_MANAGER');
--rollback not required

--changeset rakhimbaev:changeset-2
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'COUNTRY';
create table country(
    id numeric(22,0) primary key,
    name varchar(256) not null unique
);
create sequence seq_country start with 1;
--rollback not required

--changeset rakhimbaev:changeset-3
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'INCUBATOR';
create table incubator(
    id numeric(22,0) primary key,
    country_id numeric(22,0) not null,
    manager_id numeric(22,0) not null,
    name varchar(256) not null unique,
    founded timestamp not null,
    founder varchar(256) not null,
    constraint fk_country foreign key (country_id) references country(id),
    constraint fk_manager foreign key (manager_id) references users(id)
);
create sequence seq_incubator start with 1;
--rollback not required

--changeset rakhimbaev:changeset-4
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'INCUBATOR_PROJECT';
create table incubator_project(
    id numeric(22,0) primary key,
    name varchar(256) not null,
    incubator_id numeric(22,0) not null,
    started_date timestamp not null,
    end_date timestamp,
    constraint fk_incubator foreign key (incubator_id) references incubator(id) on delete cascade
);
create sequence seq_inc_project start with 1;
--rollback not required

--changeset rakhimbaev:changeset-5
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'CLIENT';
create table CLIENT(
                        id numeric(22,0) primary key,
                        project_id numeric(22,0) not null,
                        applications numeric(22,0) not null,
                        accepted numeric(22,0) not null,
                        graduated numeric(22,0) not null,
                        failed numeric(22,0) not null,
                        constraint fk_client_project foreign key (project_id) references incubator_project(id) on delete cascade
);
create sequence seq_client start with 1;
--rollback not required

--changeset rakhimbaev:changeset-6
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'REVENUE';
create table REVENUE(
                       id numeric(22,0) primary key,
                       project_id numeric(22,0) not null,
                       lease_revenue numeric(22,2) not null,
                       services_revenue numeric(22,2) not null,
                       sponsorship_revenue numeric(22,2) not null,
                       grant_revenue numeric(22,2) not null,
                       constraint fk_revenue_project foreign key (project_id) references incubator_project(id) on delete cascade
);
create sequence seq_revenue start with 1;
--rollback not required

--changeset rakhimbaev:changeset-7
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'CONSUMED_RESOURCES';
create table CONSUMED_RESOURCES(
                        id numeric(22,0) primary key,
                        project_id numeric(22,0) not null,
                        involved_managers numeric(22,0) not null,
                        involved_coaches numeric(22,0) not null,
                        involved_mentors numeric(22,0) not null,
                        used_services numeric(22,0) not null,
                        rent_space numeric(22,2) not null,
                        constraint fk_resources_project foreign key (project_id) references incubator_project(id) on delete cascade
);
create sequence seq_resources start with 1;
--rollback not required

--changeset rakhimbaev:changeset-8
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'INVESTMENT';
create table INVESTMENT(
                                   id numeric(22,0) primary key,
                                   project_id numeric(22,0) not null,
                                   investors_count numeric(22,0) not null,
                                   percentage_of_invested_clients numeric(22,0) not null,
                                   constraint fk_investment_project foreign key (project_id) references incubator_project(id) on delete cascade
);
create sequence seq_investment start with 1;
--rollback not required

--changeset rakhimbaev:changeset-9
--preconditions onFail:MARK_RAN onError:HALT onUpdateSql:FAIL
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where upper(table_name) = 'EXPENSE';
create table EXPENSE(
                           id numeric(22,0) primary key,
                           project_id numeric(22,0) not null,
                           marketing numeric(22,2) not null,
                           payroll numeric(22,2) not null,
                           equipment numeric(22,2) not null,
                           utilities numeric(22,2) not null,
                           material numeric(22,2) not null,
                           insurance numeric(22,2) not null,
                           constraint fk_expense_project foreign key (project_id) references incubator_project(id) on delete cascade
);
create sequence seq_expense start with 1;
--rollback not required