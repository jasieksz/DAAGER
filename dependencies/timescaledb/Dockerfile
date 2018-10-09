FROM timescale/timescaledb

ENV PGDATA=/var/lib/postgresql/data/timescaledb
VOLUME /var/lib/postgresql
EXPOSE 5432/tcp

ADD init.sh /docker-entrypoint-initdb.d/init.sh
