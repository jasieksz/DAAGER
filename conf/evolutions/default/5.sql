# --- !Ups
TRUNCATE TABLE "thread_infos" ;

ALTER TABLE "thread_infos" ADD "thread_daemon_thread_count" BIGINT NOT NULL;
ALTER TABLE "thread_infos" ADD "thread_total_started_thread_count" BIGINT NOT NULL;

# --- !Downs
TRUNCATE TABLE "thread_infos" ;

ALTER TABLE "thread_infos" DROP "thread_daemon_thread_count";
ALTER TABLE "thread_infos" DROP "thread_total_started_thread_count";