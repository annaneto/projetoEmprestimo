# Estágio 1: Compilação com JDK 25
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B || true

COPY src src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Runtime leve com JRE 25
FROM eclipse-temurin:25-jre
WORKDIR /app

RUN mkdir -p /app/dados && chmod 777 /app/dados

COPY --from=builder /workspace/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
