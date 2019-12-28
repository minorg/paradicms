FROM secoresearch/fuseki

RUN rm /fuseki-base/configuration/assembler.ttl
COPY /assembler.ttl /fuseki-base/configuration/

ENV ADMIN_PASSWORD xWJwxehQADhT
ENV ENABLE_DATA_WRITE true
ENV ENABLE_UPDATE true
ENV ENABLE_UPLOAD true

VOLUME ["/fuseki-base/databases/"]
