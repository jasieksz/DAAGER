# --- !Ups

create table "network_infos" (
"timestamp" TIMESTAMP with time zone NOT NULL,
"address" VARCHAR NOT NULL,
"tcp_connection_active_count" BIGINT NOT NULL,
"tcp_connection_client_count" BIGINT NOT NULL,
"tcp_connection_count" BIGINT NOT NULL
);

create table "os_infos" (
"timestamp" TIMESTAMP with time zone NOT NULL,
"address" VARCHAR NOT NULL,
"os_process_cpu_load" DOUBLE PRECISION NOT NULL,
"os_system_load_average" DOUBLE PRECISION NOT NULL,
"os_system_cpu_load" DOUBLE PRECISION NOT NULL,
"os_total_physical_memory_size" BIGINT NOT NULL,
"os_free_physical_memory_size" BIGINT NOT NULL,
"os_free_swap_space_size" BIGINT NOT NULL,
"os_total_swap_space_size" BIGINT NOT NULL
);

create table "thread_infos" (
"timestamp" TIMESTAMP with time zone NOT NULL,
"address" VARCHAR NOT NULL,
"thread_peak_count" BIGINT NOT NULL,
"thread_thread_count" BIGINT NOT NULL
);

create table "runtime_infos" (
"timestamp" TIMESTAMP with time zone NOT NULL,
"address" VARCHAR NOT NULL,
"runtime_available_processors" BIGINT NOT NULL,
"runtime_total_memory" BIGINT NOT NULL,
"runtime_max_memory" BIGINT NOT NULL,
"runtime_free_memory" BIGINT NOT NULL,
"runtime_used_memory" BIGINT NOT NULL
);


# --- !Downs

drop table "network_infos";
drop table "os_infos";
drop table "thread_infos";
drop table "runtime_infos";

