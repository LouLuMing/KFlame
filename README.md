# GWeb
反向代理软件，体积800k，没有任何依赖，接口代理性能高于nginx，文件代理性能差不多
可运行程序bin.tar,解压，配置后直接运行
tar xvf bin.tar
修改myAnt.xml
配置端口localport 8989
配置映射路径
映射文件
url="/static" /mnt/sdb/work/jxzhly/web/dist
映射接口
url="/" http://192.168.1.144:9000
启动项目
运行代理./service.sh start
关闭代理./service.sh stop