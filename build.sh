#!/bin/bash
#tags="`date +%Y%m%d.%H%M%S`"
tags="latest"

echo '######开始构建项目######'
git pull
mvn clean install -Dmaven.test.skip=true
echo '######开始构建镜像######'
docker build -t  antennababy/download_transport:$tags .
echo '######开始登陆dockerHub######'
docker login -u=$1 -p=$2
echo '######开始提交镜像######'
docker push antennababy/download_transport:$tags;

