#!/usr/bin/env bash

psql --username "$POSTGRES_USER"  <<EOF
create user daager with password 'daager';
CREATE DATABASE daager WITH OWNER daager;
GRANT ALL PRIVILEGES ON DATABASE daager TO daager;
\c daager
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;
EOF