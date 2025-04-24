FROM mongo:8.0.6
ENV MONGO_INITDB_ROOT_USERNAME=exampleUser
ENV MONGO_INITDB_ROOT_PASSWORD=examplePassword

COPY ./init.js /docker-entrypoint-initdb.d/init.js