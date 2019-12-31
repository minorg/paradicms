FROM secoresearch/fuseki

RUN rm /fuseki-base/configuration/assembler.ttl
COPY /assembler.ttl /fuseki-base/configuration/

ENV ADMIN_PASSWORD xWJwxehQADhT

VOLUME ["/fuseki-base/databases/"]
