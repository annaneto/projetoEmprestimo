# Estágio 1: Compilação com JDK 25
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace

# Copia arquivos de configuração e wrapper do Maven
COPY emprestimo/.mvn .mvn
COPY emprestimo/mvnw .
COPY emprestimo/pom.xml .

# Permissão de execução para o mvnw
RUN chmod +x ./mvnw

# Baixa dependências para cache
RUN ./mvnw dependency:go-offline -B || true

# Copia o código-fonte e gera o pacote
COPY emprestimo/src src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Runtime leve com JRE 25
FROM eclipse-temurin:25-jre
WORKDIR /app

# Diretório para dados do H2
RUN mkdir -p /app/dados && chmod 777 /app/dados

# Copia o artefato executável
COPY --from=builder /workspace/target/*.jar app.jar

# Variável de porta usada pelo Render
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
