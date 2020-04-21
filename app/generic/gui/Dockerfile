# Build
FROM tiangolo/node-frontend:10 as build
WORKDIR /app
COPY /package.json .
COPY /lerna.json .
COPY /lib/ts ./lib/ts
COPY /app/generic/gui ./app/generic/gui
RUN npm install
RUN npm run lerna:bootstrap
RUN cd lib/ts/base && npm run build
# RUN cd app/generic/gui && npm run build
RUN cd app/generic/gui && npm run build-development-unix

# Deployment
FROM nginx:1.17.6

COPY /app/generic/nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/app/generic/gui/dist /usr/share/nginx/html

EXPOSE 80
