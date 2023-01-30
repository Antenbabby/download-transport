#!/bin/bash
#tags="`date +%Y%m%d.%H%M%S`"
tags="latest"

echo '######开始构建项目######'
git pull
mvn clean install -Dmaven.test.skip=true
echo '######开始构建镜像######'
#docker build -t  antennababy/download_transport:$tags .
#docker build --platform linux/arm64 -t  antennababy/download_transport:$tags .
echo '######开始登陆dockerHub######'
docker login
echo '######开始提交镜像######'
#docker push antennababy/download_transport:$tags;
docker buildx build -t  antennababy/download_transport --platform=linux/arm,linux/arm64,linux/amd64 . --push
