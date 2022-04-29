FROM openjdk:17-alpine as builder

ARG SKIP_TESTS=false
ARG MAVEN_PROFILE=""
ARG MAVEN_PARAMS=""
ARG MVN_TRANSFER_LOG_LEVEL="warn"

# Define environment variables
ENV TIMEZONE=UTC \
    BUILD_DIR=/var/build \
    # Speed up Maven JVM
    MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=${MVN_TRANSFER_LOG_LEVEL}" \
    BUILD_PARAMS=""

USER root

RUN adduser --home /home/service --disabled-password --gecos "First Last,RoomNumber,WorkPhone,HomePhone" --disabled-password svcuser

RUN mkdir -p ${BUILD_DIR} && chown -R svcuser:svcuser ${BUILD_DIR}

USER svcuser

WORKDIR ${BUILD_DIR}

# copy the project files
COPY --chown=svcuser:svcuser .mvn/ ${BUILD_DIR}/.mvn/
COPY --chown=svcuser:svcuser  mvn* ${BUILD_DIR}/
COPY --chown=svcuser:svcuser  pom.xml ${BUILD_DIR}/

# build all dependencies
RUN chmod +x ./mvnw && \
    ./mvnw org.apache.maven.plugins:maven-dependency-plugin:3.3.0:go-offline --batch-mode && \
    ./mvnw clean dependency:go-offline --no-snapshot-updates --batch-mode

COPY --chown=svcuser:svcuser ./ ${BUILD_DIR}/

RUN chmod +x ./mvnw && \
    if [ "x$SKIP_TESTS" = "xtrue" ] ; then export BUILD_PARAMS="-DskipTests"; fi && \
    if [ ! -z ${MAVEN_PROFILE} ] ; then export MAVEN_PROFILE="-P ${MAVEN_PROFILE}"; fi && \
    echo "Running command in "$(pwd)": ./mvnw clean package ${MAVEN_PROFILE} --batch-mode -DAPP_LOG_LEVEL=INFO ${BUILD_PARAMS} ${MAVEN_PARAMS}" && \
	./mvnw -nsu clean package ${MAVEN_PROFILE} --batch-mode -DAPP_LOG_LEVEL=INFO ${BUILD_PARAMS} ${MAVEN_PARAMS}

RUN mkdir -p ${BUILD_DIR}/target/layered

WORKDIR ${BUILD_DIR}/target/layered

RUN java -Djarmode=layertools -jar ${BUILD_DIR}/target/*.jar extract

# Final image
FROM openjdk:17-alpine

# Define environment variables
ENV TIMEZONE=UTC \
    BUILD_DIR=/var/build \
    APP_DIR=/home/service/application \
    APP_ENVIRONMENT=""

# Install packages
RUN apk add --update --no-cache \
    bash \
    tzdata

# Set time zone
RUN ln -snf /usr/share/zoneinfo/${TIMEZONE} /etc/localtime && echo ${TIMEZONE} > /etc/timezone

RUN adduser --home /home/service --disabled-password --gecos "First Last,RoomNumber,WorkPhone,HomePhone" --disabled-password svcuser

USER svcuser

WORKDIR ${APP_DIR}

COPY --from=builder ${BUILD_DIR}/target/layered/dependencies ./
COPY --from=builder ${BUILD_DIR}/target/layered/spring-boot-loader ./
COPY --from=builder ${BUILD_DIR}/target/layered/snapshot-dependencies ./
COPY --from=builder ${BUILD_DIR}/target/layered/application ./

CMD ["java", "-Dspring.profiles.active=${APP_ENVIRONMENT}", "org.springframework.boot.loader.JarLauncher"]