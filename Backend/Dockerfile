FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# gradlew 실행 권한 부여 및 빌드 (테스트 제외)
RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# 빌드된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# Render에서 사용할 포트
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
