#!/bin/bash 

echo "打包文件"
yarn build
echo "传输文件"


scp -r ./dist/** root@xxx:/www/wwwroot/notice.wlcb.mobi



echo "部署成功"