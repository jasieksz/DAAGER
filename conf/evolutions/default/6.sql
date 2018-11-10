# --- !Ups

create table "log_events" (
"timestamp" TIMESTAMP with time zone NOT NULL,
"address" VARCHAR NOT NULL,
"cluster_id" VARCHAR NOT NULL,
"message" VARCHAR NOT NULL,
"logger_name" VARCHAR NOT NULL,
"log_level" VARCHAR NOT NULL
);

# --- !Downs

drop table "log_events";