FROM registry.cn-hangzhou.aliyuncs.com/lhn/jre2:1.8
MAINTAINER lhn <cabopkll@sina.com>

VOLUME /var/sytech/web/content
COPY smartmining.jar /usr/sytech/web/run.jar
EXPOSE 80
CMD  ["java","-Djava.security.egd=file:/dev/./urandom","-Dfile.encoding=UTF-8","-Duser.timezone=GMT+08","-jar","/usr/sytech/web/run.jar","--spring.profiles.active=dev"]
