# Etapa 1: Build da aplicação
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
# Dá permissão de execução e compila o projeto pulando os testes (já testados no GitHub Actions)
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa 2: Imagem final leve para produção
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia apenas o .jar compilado da etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
