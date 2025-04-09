FROM confluentinc/cp-kafka:7.9.0
COPY ./config/server.properties /opt/kafka/config/server.properties

ENTRYPOINT ["sh", "-c", "rm -rf /var/lib/kafka/data/* && kafka-server-start /opt/kafka/config/server.properties"]
