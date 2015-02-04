DROP SCHEMA PUBLIC CASCADE;

CREATE TABLE USER_ACCOUNT
(
  ID            BIGINT NOT NULL,
  LOGIN         CHARACTER VARYING(128),
  EMAIL         CHARACTER VARYING(128),
  FIRST_NAME    CHARACTER VARYING(128),
  LAST_NAME     CHARACTER VARYING(128),
  PASSWORD_HASH CHARACTER VARYING(128),
  SALT          CHARACTER VARYING(128),
  CONSTRAINT USER_ACCOUNT_PKEY PRIMARY KEY (ID)
);

CREATE SEQUENCE USER_ACCOUNT_IDS;

CREATE TABLE ARTIFACT (
  ID               BIGINT NOT NULL PRIMARY KEY,
  NAME             VARCHAR(256),
  DESCRIPTION      VARCHAR(1024),
  TIME_START       TIMESTAMP,
  TIME_END         TIMESTAMP,
  CUBE_COORDINATES VARCHAR(2048)
);

CREATE SEQUENCE ARTIFACT_IDS;