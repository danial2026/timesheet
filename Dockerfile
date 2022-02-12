FROM adoptopenjdk/openjdk11:latest
MAINTAINER danial "sadcat@tutanota.com"

EXPOSE 9000
RUN mkdir /opt/timesheet-module
WORKDIR ./timesheet-module
COPY ./target/timesheet-0.0.1-SNAPSHOT.jar /opt/timesheet-module
CMD ["java", "-jar", "-Dspring.profiles.active=dev", "/opt/timesheet-module/timesheet-0.0.1-SNAPSHOT.jar"]