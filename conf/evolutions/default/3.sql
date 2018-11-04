# --- !Ups
TRUNCATE TABLE "network_infos" ;
TRUNCATE TABLE "os_infos" ;
TRUNCATE TABLE "thread_infos" ;
TRUNCATE TABLE "runtime_infos" ;


ALTER TABLE "network_infos" ADD "cluster_id" VARCHAR NOT NULL;
ALTER TABLE "os_infos" ADD "cluster_id" VARCHAR NOT NULL;
ALTER TABLE "thread_infos" ADD "cluster_id" VARCHAR NOT NULL;
ALTER TABLE "runtime_infos" ADD "cluster_id" VARCHAR NOT NULL;

# --- !Downs
ALTER TABLE "network_infos" DROP "cluster_id";
ALTER TABLE "os_infos" DROP "cluster_id";
ALTER TABLE "thread_infos" DROP "cluster_id";
ALTER TABLE "runtime_infos" DROP "cluster_id";
