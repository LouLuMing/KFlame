# GWeb
反向代理软件，体积小，没有依赖，接口代理性能高于nginx
tar xvf bin.tar
修改myAnt.xml
配置端口<localport>8989</localport>
配置映射路径
映射文件
<resource url="/satic">/mnt/sdb/work/jxzhly/web/dist</resource>
映射接口
<resource url="/">http://192.168.1.144:9000</resource>
启动项目
运行代理./service.sh start
关闭代理./service.sh stop
