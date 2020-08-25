# GWeb
框架核心是基于Nio的网络框架和基于状态机的协议解析  
  基于Nio的框架的组件   
    Proxy反向代理，com.china.fortune.proxy  
    Web容器，com.china.fortune.restfulHttpServer  
    WebSocketServer，com.china.fortune.http.websocket  
    TcpRouter 端口映射，com.china.fortune.tcpRouter  
    ChannelServer, ChannelClient 内网映射到外网工具，com.china.fortune.tcpRouter  
  基于状态机的组件  
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
    映射文件：  
    <resource gzip="1" cache="1" url="/static">/home</resource>  
    http://xx.xx.xx:8989/static/A.js 映射成/home/static/A.js  
    gzip:0禁止gzip压缩，1开启gzip压缩  
    cache:0禁止缓存，1开启缓存  
    映射接口：  
    <resource url="/showhttp">http://20.21.1.170:8990</resource>  
    http://xx.xx.xx:8989/showhttp 映射成http://20.21.1.170:8990/showhttp  
    可映射多个地址,以;隔开  
    <resource url="/showhttp">http://20.21.1.170:8990;http://20.21.1.123:8990</resource>  
  动态配置接口：  
    查看配置信息  
    http://xx.xx.xx:8989/proxy/show  
    新增配置信息  
    http://xx.xx.xx:8989/proxy/add?resource=/account&path=http://127.0.0.1:8600  
    删除配置信息
    http://xx.xx.xx:8989/proxy/del?resource=/account&path=http://127.0.0.1:8600  
    刷新缓存  
    http://xx.xx.xx:8989/proxy/cache?path=/home  
  启动项目  
  运行代理./service.sh start  
  关闭代理./service.sh stop  
