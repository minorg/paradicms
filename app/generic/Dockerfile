# Build back end
FROM hseeberger/scala-sbt:11.0.4_1.3.5_2.12.10 as build-app
WORKDIR /app
COPY /build.sbt .
COPY /project/ ./project
COPY /lib/scala ./lib/scala/
COPY /app/generic/ ./app/generic/
RUN sbt "project genericApp" playUpdateSecret dist

# Build front end
FROM tiangolo/node-frontend:10 as build-gui
WORKDIR /app
COPY /package.json .
COPY /lerna.json .
COPY /lib/ts ./lib/ts
COPY /app/generic/gui ./app/generic/gui
RUN npm install
RUN npm run lerna:bootstrap
RUN cd lib/ts/generic && npm run build
RUN cd app/generic/gui && npm run build

# Deployment
FROM ubuntu:18.04

RUN apt-get update && apt-get install -y unzip

COPY /app/generic/nginx.conf /etc/nginx/sites-available/default

COPY --from=build-app /app/app/generic/target/universal/generic-app-1.0.0-SNAPSHOT.zip /
RUN unzip -q generic-app-1.0.0-SNAPSHOT.zip && mv /generic-app-1.0.0-SNAPSHOT /app && chmod +x /app/bin/generic-app

COPY --from=build-gui /app/app/generic/gui/dist /usr/share/nginx/html

EXPOSE 9000

CMD ["/app/bin/generic-app"]
