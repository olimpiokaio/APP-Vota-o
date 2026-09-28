# ==============================================================================
# Etapa 1: Build da Aplicação com Java 21 e Maven
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache de dependências do Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compilação e empacotamento do JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==============================================================================
# Etapa 2: Imagem Final de Execução (Runtime Único para DES, TES e PROD)
# Imagem imutável com parametrização de ambiente em tempo de execução via SPRING_PROFILES_ACTIVE
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Criar usuário não-root para segurança corporativa
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup

# Copiar artefato compilado
COPY --from=builder /build/target/*.jar app.jar

# Variáveis de ambiente padrão para runtime
ENV SPRING_PROFILES_ACTIVE=des \
    PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
