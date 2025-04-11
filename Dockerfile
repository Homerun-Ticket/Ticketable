# 베이스 이미지 설정
FROM eclipse-temurin:17-jre-alpine

# 이미지를 만든 사람
LABEL maintainer="Inhak <inhak1122@google.com>"

# 빌드된 JAR 파일 복사
COPY build/libs/*.jar app.jar

# 애플리케이션 실행 포트
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]