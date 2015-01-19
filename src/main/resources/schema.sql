CREATE TABLE user_account
(
  id bigint NOT NULL,
  login character varying(128),
  email character varying(128),
  first_name character varying(128),
  last_name character varying(128),
  password_hash character varying(128),
  salt character varying(128),
  CONSTRAINT user_account_pkey PRIMARY KEY (id)
);

create sequence user_account_ids;