#Build Stage
FROM eclipse-temurin:25-jdk-jammy AS build

WORKDIR /app
COPY mvnm .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

#Runtime stage
FROM eclipse-temurin:25-jdk-jammy
ARG PROFILE=dev
ARG APP_VERSION=1.0.0

WORKDIR /app
COPY --from=build /app/target/*.jar /app/

EXPOSE 8080

ENV DB_URL=jdbc:postgresql://spring_sec_asymmetric:5432/spring_app_db
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}

CMD java -jar -Dsrping.profiles.active=${ACTIVE_PROFILE} spring-security-asymmetric-encryption-${JAR_VERSION}.jar