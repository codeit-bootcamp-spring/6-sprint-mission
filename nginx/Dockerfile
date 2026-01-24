FROM nginx:alpine

RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY ./html /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]