FROM keycloak/keycloak:25.0.1

COPY ./target/*.jar /opt/keycloak/providers/custom-user-provider.jar
COPY ./target/libs/*.jar /opt/keycloak/providers
COPY ./quarkus.properties /opt/keycloak/conf/quarkus.properties