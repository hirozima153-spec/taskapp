# ---- build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# ---- run ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# デモ用：メモリDB＋サンプルデータ（再起動で初期状態に戻る）
ENV DB_URL=jdbc:h2:mem:taskdb;DB_CLOSE_DELAY=-1 \
    SEED_SAMPLE_DATA=true
EXPOSE 8080
# 無料枠(512MB)向けにメモリを節約
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=65", "-Xss512k", "-XX:+UseSerialGC", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]
