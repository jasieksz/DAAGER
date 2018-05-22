# --- !Ups

create table "node_healths" (
  "node_state" VARCHAR NOT NULL,
  "cluster_state" VARCHAR NOT NULL,
  "cluster_safe" BOOLEAN NOT NULL,
  "migration_queue_size" INTEGER NOT NULL,
  "cluster_size" INTEGER NOT NULL,
  "timestamp" TIMESTAMP NOT NULL,
  "node_id" VARCHAR NOT NULL
);
alter table "node_healths" add constraint "node_health_pk" primary key("timestamp","node_id");

# --- !Downs

drop table node_healths;