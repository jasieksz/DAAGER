# --- !Ups

create table clusters(
  cluster_id TEXT NOT NULL PRIMARY KEY,
  alias TEXT NOT NULL,
  base_address TEXT NOT NULL,
  is_active BOOLEAN NOT NULL
)

# --- !Downs

drop table clusters;