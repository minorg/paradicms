FROM hseeberger/scala-sbt:11.0.4_1.3.5_2.12.10 as build
WORKDIR /app
COPY . /app
RUN sbt playUpdateSecret dist


FROM openjdk:11-jre

COPY --from=build /app/target/universal/core-1.0.0-SNAPSHOT.zip /
RUN unzip -q core-1.0.0-SNAPSHOT.zip && mv /core-1.0.0-SNAPSHOT /app && chmod +x /app/bin/core

EXPOSE 9000

CMD ["/app/bin/core"]
