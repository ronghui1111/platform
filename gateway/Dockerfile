# Docker image for springboot file run
FROM java:8
# 作者
MAINTAINER Mr.Rong
# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
VOLUME /tmp
# 将jar包添加到容器中并更名为app.jar
ADD target/study-1.0.0-SNAPSHOT.jar study.jar
# 运行jar包
RUN bash -c 'touch /study.jar'
ENTRYPOINT ["java","-jar","/study.jar"]
