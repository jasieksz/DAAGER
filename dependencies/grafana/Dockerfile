FROM grafana/grafana

EXPOSE 3000/tcp

ADD grafana.ini /etc/grafana/grafana.ini
ADD --chown=777 grafana.db /var/lib/grafana/grafana.db