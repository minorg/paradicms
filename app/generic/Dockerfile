# Build
FROM hseeberger/scala-sbt:11.0.4_1.3.5_2.12.10 as build
WORKDIR /app
COPY /build.sbt .
COPY /project/ ./project
COPY /lib/scala ./lib/scala/
COPY /app/generic/ ./app/generic/
RUN sbt "project genericApp" playUpdateSecret dist

# Deployment
FROM ubuntu:18.04

RUN apt-get update && apt-get install -y default-jre-headless unzip

COPY --from=build /app/app/generic/target/universal/generic-app-1.0.2-SNAPSHOT.zip /
RUN unzip -q generic-app-1.0.2-SNAPSHOT.zip && mv /generic-app-1.0.2-SNAPSHOT /app && chmod +x /app/bin/generic-app

EXPOSE 9000

ENTRYPOINT ["/app/bin/generic-app"]
