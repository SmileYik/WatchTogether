# WatchTogether
一起看电视咯！

异地一起看电视, 能一起看电视的前提是能够提供一个有效的视频链接，故在一起看电视的主要功能上添加了一个附属功能也就是查找链接中的视频链接

### 体验一下

[我的博客](https://blog.smileyik.tk/watchtogether)

### 关于搭建

这是一个springboot项目，所以自备好java开发环境哦

#### 1. 修改application.properties

```properties
# 配置selenium，这里用的是chrome的驱动，填上chrome驱动路径
watchtogether.chromeDriverPath=chromedriver
# 搜索视频使用的token
watchtogether.token=123456
# 没有使用token，两次搜索之间的间隔时间（毫秒）
watchtogether.videoDogCoolDown=300000
```

#### 2. 修改index.html

index.html 在资源目录下的static目录下, 将里面的`localhost`改为你的后端地址即可

#### 3. 构建jar包

使用如下指令去构建jar包

```
./mvn package
```

### 如何使用

访问前端页面，输入房间号连接到房间服务器即可，如果房间号不存在则会自动创建一个新的房间，
并且成为此房间的主机，主机可以设定要进行播放的视频链接和播放进度，观看者可以选择同步主机当前的观看进度

前端页面中也有写使用说明，可以去[我的博客](https://blog.smileyik.tk/watchtogether)查看




