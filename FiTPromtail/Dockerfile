FROM grafana/promtail:main-c9ef062
COPY ./config.yml /etc/promtail/config.yml
CMD ["-config.file=/etc/promtail/config.yml", "-config.expand-env=true", "-print-config-stderr"]
