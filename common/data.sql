create database notification_database;

--- System user profile

create table system_user_profile
(
  profile_id serial primary key,
  username   VARCHAR(32) not null,
  password   VARCHAR(32) not null
);

--- User profile

create table user_profile
(
  user_id       varchar(512) not null,
  email_address varchar(512) not null,
  is_active     boolean               default true,
  node_id       varchar(128) not null,
  last_update   TIMESTAMP    not null default Now()
);

create unique index user_profile_index
  on user_profile (user_id);

--- Notification template

create table notification_template
(
  template_id        varchar(36)  not null,
  template_title     varchar(256) not null,
  notification_title text,
  notification       text,
  last_update        TIMESTAMP    not null default Now()
);

create unique index notification_template_index
  on notification_template (template_id);

--- Sending data table

create table sending_info
(
  user_id     varchar(512) not null,
  message_id  varchar(512) not null,
  template_id varchar(36)  not null,
  status      varchar(36)  not null,
  last_update TIMESTAMP    not null default Now()
);

--- Security entity

create table security_node
(
  row_id           varchar(64)  not null,
  port             varchar(7)   not null,
  ip_address       varchar(256) not null,
  domain_name      varchar(256),
  connection_type  varchar(7)   not null,
  security_type    varchar(36)  not null,
  application_name varchar(256) not null,
  security_token   varchar(512) not null
);

create unique index security_node_index
  on security_node (ip_address);