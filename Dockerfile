FROM openjdk:8-jdk-alpine
RUN apk --no-cache add curl && echo "Asia/Shanghai" > /etc/timezone

RUN mkdir /data && mkdir /data/clusters && mkdir /data/apps && mkdir /data/charts

RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl && \
    chmod +x ./kubectl && \
    mv ./kubectl /usr/bin/kubectl

ARG JAR_FILE
COPY target/${JAR_FILE} app.jar
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
ENV APP_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app.jar $APP_OPTS"]
