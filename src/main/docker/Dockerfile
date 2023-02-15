FROM eclipse-temurin:17-jdk-focal as build

COPY . repository
RUN cd /repository && \
    ./gradlew build && \
    tar xf /repository/build/distributions/ordalia.tar -C /opt

FROM eclipse-temurin:17-jre-focal

RUN groupadd -g 9000 ordalia && \
    useradd -u 9000 -g ordalia ordalia-user

USER ordalia-user:ordalia
COPY --from=build /opt/ordalia /opt/ordalia

EXPOSE 8080
CMD ["/opt/ordalia/bin/ordalia"]