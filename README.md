# GWeb
框架核心是基于Nio的网络框架和基于状态机的协议解析
  基于Nio的框架的组件有
    Proxy反向代理，com.china.fortune.proxy
    Web容器，com.china.fortune.restfulHttpServer
    WebSocketServer，com.china.fortune.http.websocket
    TcpRouter 端口映射，com.china.fortune.tcpRouter
    ChannelServer, ChannelClient 内网映射到外网工具，com.china.fortune.tcpRouter
  基于状态机的组件：
    Json解析，com.china.fortune.json
    Http解析，com.china.fortune.http.client
    Yaml解析，com.china.fortune.yaml

Proxy反向代理,使用说明
  没有任何依赖，接口代理性能高于nginx，文件代理性能差不多
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
