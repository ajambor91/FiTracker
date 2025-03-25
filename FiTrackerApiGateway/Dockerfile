FROM nginx:alpine

EXPOSE 8090:80
EXPOSE 443:443
COPY ./nginx.conf /etc/nginx/nginx.conf
CMD ["nginx", "-g", "daemon off;"]
