FROM openjdk:8u212-jre-alpine3.9
RUN rm -f /etc/localtime \
&& ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezoneVOLUME /tmp
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories
RUN  apk update \
    &&  apk add aria2
COPY target/*.jar app.jar
VOLUME /downloadTransport
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -jar /app.jar