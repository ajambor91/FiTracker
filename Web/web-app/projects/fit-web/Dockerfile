FROM node:22 AS BUILDER


WORKDIR /app
COPY ./web-app .
RUN npm install -g @angular/cli
RUN npm install && ng b api && ng b fit-web configuration=production

FROM nginx:latest AS RUNNER

COPY ./web-app/projects/fit-web/nginx.conf /etc/nginx/nginx.conf
WORKDIR /app

COPY --from=BUILDER /app/dist/fit-web /app

RUN chown -R nginx:nginx /app/browser && chmod -R 777 /app/browser
