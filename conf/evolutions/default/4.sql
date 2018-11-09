# --- !Ups
TRUNCATE TABLE "os_infos" ;

ALTER TABLE "os_infos" DROP "os_process_cpu_load";
ALTER TABLE "os_infos" DROP "os_system_cpu_load";
ALTER TABLE "os_infos" DROP "os_total_physical_memory_size";
ALTER TABLE "os_infos" DROP "os_free_physical_memory_size";
ALTER TABLE "os_infos" DROP "os_free_swap_space_size";
ALTER TABLE "os_infos" DROP "os_total_swap_space_size";

ALTER TABLE "os_infos" ADD "disk_usable_space" BIGINT NOT NULL;
ALTER TABLE "os_infos" ADD "disk_free_space" BIGINT NOT NULL;
ALTER TABLE "os_infos" ADD "disk_total_space" BIGINT NOT NULL;


# --- !Downs
TRUNCATE TABLE "os_infos" ;

ALTER TABLE "os_infos" DROP "disk_usable_space";
ALTER TABLE "os_infos" DROP "disk_free_space";
ALTER TABLE "os_infos" DROP "disk_total_space";

ALTER TABLE "os_infos" ADD "os_process_cpu_load" DOUBLE PRECISION NOT NULL,
ALTER TABLE "os_infos" ADD "os_system_load_average" DOUBLE PRECISION NOT NULL,
ALTER TABLE "os_infos" ADD "os_system_cpu_load" DOUBLE PRECISION NOT NULL,
ALTER TABLE "os_infos" ADD "os_total_physical_memory_size" BIGINT NOT NULL,
ALTER TABLE "os_infos" ADD "os_free_physical_memory_size" BIGINT NOT NULL,
ALTER TABLE "os_infos" ADD "os_free_swap_space_size" BIGINT NOT NULL,
ALTER TABLE "os_infos" ADD "os_total_swap_space_size" BIGINT NOT NULL