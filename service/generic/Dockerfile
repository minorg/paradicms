FROM hseeberger/scala-sbt:11.0.4_1.3.5_2.12.10 as build
WORKDIR /app
COPY /build.sbt .
COPY /project/ ./project
COPY /service/lib/ ./service/lib/
COPY /service/generic/ ./service/generic/
RUN sbt "project generic" playUpdateSecret dist


FROM openjdk:11-jre

COPY --from=build /app/service/generic/target/universal/generic-1.0.0-SNAPSHOT.zip /
RUN unzip -q generic-1.0.0-SNAPSHOT.zip && mv /generic-1.0.0-SNAPSHOT /app && chmod +x /app/bin/generic

EXPOSE 9000

CMD ["/app/bin/generic"]
