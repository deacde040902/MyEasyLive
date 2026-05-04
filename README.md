# EasyLive系统架构图
<img width="1307" height="888" alt="image" src="https://github.com/user-attachments/assets/187a7396-3d83-465a-9fb3-c77249f56cde" />

## 微服务模块说明

| 服务名称 | 端口 | 功能描述 |
|---------|------|---------|
| easylive-cloud-admin | 7070 | 后台管理服务 |
| easylive-cloud-getaway | 7071 | API 网关，统一路由入口 |
| easylive-cloud-resource | 7072 | 文件资源服务(上传、存储、HLS转码) |
| easylive-cloud-interact | 7073 | 互动服务(评论、弹幕、实时聊天) |
| easylive-cloud-web | 7074 | 核心业务服务(用户、视频、支付等) |
| easylive-cloud-base | - | 基础模块(实体、Mapper、Service) |
| easylive-cloud-conmon | - | 公共模块(代码生成器、公共类等等) |

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.18 + Spring Cloud 2021.0.6 + Spring Cloud Alibaba 2021.0.5.0
- **网关**: Spring Cloud Gateway
- **注册/配置**: Nacos
- **数据库**: MySQL 8.0 + MyBatis-Plus 3.5.3.1
- **缓存**: Redis
- **搜索**: Elasticsearch
- **限流熔断**: Sentinel
- **分布式事务**: Seata
- **消息队列**: RocketMQ
- **实时通信**: Netty (WebSocket)
- **第三方登录**: QQ OAuth
- **邮箱服务**: QQ邮箱
### 前端
- **框架**: Vue 3
- **构建工具**: Vite
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **视频播放**: hls.js

## 启动流程
### 1.确保以下环境运行-完整服务的关键
| 服务 | 默认端口 | 说明 |
|---------|------|---------|
MySQL| 3306 |数据库，确保 easylive 数据库已创建 |
Redis |6379 |缓存服务 |
Nacos| 8848 |服务注册与配置中心|
Seata| 9080 |分布式事务解决方案（TC 服务）|
Sentinel| 8081| 限流熔断控制台|
RocketMQ |9876/10911 |消息队列|
Elasticsearch| 9200| 全文搜索|
### 2.后端-微服务启动顺序
##### 按照依赖关系，依次启动以下服务：所有端口都将被网关代理至7071
1. easylive-cloud-admin        (管理后台: 7070)
2. easylive-cloud-base        (基础模块，被其他服务依赖)
3. easylive-cloud-common       (公共模块)
4. easylive-cloud-getaway      (网关: 7071)
5. easylive-cloud-resource     (资源服务: 7072)
6. easylive-cloud-interact     (互动服务: 7073)
7. easylive-cloud-web          (核心业务: 7074)

### 3.前端-启动流程
cd easylive-frontend
npm install
npm run dev
  
### 4.验证服务状态
#### 启动完成后，访问 Nacos 控制台查看服务注册状态：
http://localhost:8848/nacos
用户名: nacos
密码: nacos

## 项目演示
### 首页-（无登录状态）：上传的视频在此显示，左侧为大视频，进行轮播效果。
<img width="697" height="474" alt="image" src="https://github.com/user-attachments/assets/0e22d912-6c72-481f-b7de-b0cbb78afad0" />
### 登录界面：点击首页上方黑色默认头像进入。分别有用户登录、管理员登录和注册界面。注册使用真实qq邮箱进行验证码验证。
<img width="692" height="355" alt="image" src="https://github.com/user-attachments/assets/8e289c04-cc63-4231-b560-64b3877ad633" />
<img width="692" height="356" alt="image" src="https://github.com/user-attachments/assets/fadbbc6d-bc3c-4bdc-a17b-28211c43ad71" />
<img width="691" height="363" alt="image" src="https://github.com/user-attachments/assets/64bdb744-46be-4fb8-a30a-d1c4e2a38cd8" />
### 登录成功进入首页：可按标签进行筛选视频类型。
<img width="692" height="614" alt="image" src="https://github.com/user-attachments/assets/b7fd94a9-8589-4fc0-94e1-d05d879417fe" />
### 首页-搜索功能（登录状态下）：
<img width="691" height="304" alt="image" src="https://github.com/user-attachments/assets/09aa5299-2064-4395-891c-0cb86f223890" />
### 单击任意视频进入视频播放页：可进行点赞收藏等一下操作（登录状态下），集成了弹幕、评论、推荐视频和进入视频上传者主页和关注功能。
<img width="692" height="635" alt="image" src="https://github.com/user-attachments/assets/b9251252-177f-4110-8c34-5ad414f5b523" />
### 投稿功能：投稿成功可上传至首页进行观看。
<img width="692" height="682" alt="image" src="https://github.com/user-attachments/assets/9abffb9e-11dc-4765-9037-68a0a5ee3cfe" />
### 创作中心-发布视频
<img width="691" height="345" alt="image" src="https://github.com/user-attachments/assets/899da4ef-efc0-4797-b274-d9be500e592a" />
### 创作中心-我的视频：可进行已上传视频的编辑和删除。
<img width="691" height="386" alt="image" src="https://github.com/user-attachments/assets/e60b4d0c-9ee6-4d33-bfdf-81f4ab246a7f" />
### 创作中心-我的视频-编辑视频功能：
<img width="692" height="654" alt="image" src="https://github.com/user-attachments/assets/c2d5a9ed-779c-4c42-8741-3ba195ce77bf" />
### 创作中心-数据中心：可查看视频相应数据
<img width="692" height="281" alt="image" src="https://github.com/user-attachments/assets/4c238bfe-635f-44de-aa50-cd7466fa8a84" />
### 创作中心-账号设置：可修改自身账号相应数据
<img width="692" height="309" alt="image" src="https://github.com/user-attachments/assets/f2e7aa30-c573-4ca0-aef2-64600277a35f" />
### 历史记录：可点击视频观看或删除观看历史
<img width="691" height="355" alt="image" src="https://github.com/user-attachments/assets/4067ffaa-1a6f-414b-ace1-981c9dd22a50" />
### 消息：显示一系列消息，并且可与他人进行私聊操作
<img width="691" height="345" alt="image" src="https://github.com/user-attachments/assets/bda99bcd-566c-4b06-ad79-3e1376bcb7b3" />
<img width="691" height="345" alt="image" src="https://github.com/user-attachments/assets/2d511c68-4d81-4622-a234-0ea502aaf167" />
### 消息-私聊：可进行聊天发送信息，实现有撤回和删除消息功能。
<img width="692" height="446" alt="image" src="https://github.com/user-attachments/assets/b3e093c8-b75a-4b12-95f4-603a0eea3a8a" />
### 收藏页面：显示已收藏视频，鼠标移至视频右上角×号可进行移除收藏
<img width="692" height="341" alt="image" src="https://github.com/user-attachments/assets/284ab90b-be86-4996-a2d2-34d98c7c0d01" />
<img width="692" height="371" alt="image" src="https://github.com/user-attachments/assets/7878ed2b-bead-4563-97c4-d5acbc2c5fca" />
### 大会员：
<img width="692" height="935" alt="image" src="https://github.com/user-attachments/assets/d09bd348-b937-4394-81e5-68c321a59350" />
### 大会员-点击开通或许续费进入支付界面，通过支付宝沙箱支付。开通大会员后将在主页和首页拥有大会员标识。
<img width="660" height="979" alt="image" src="https://github.com/user-attachments/assets/930737e4-1e9b-44a9-ac73-bf633228a682" />
### 会员购：领取优惠价或开通大会员享有专属打折，可进行购买并下单，同样通过支付宝沙箱支付。
<img width="692" height="1359" alt="image" src="https://github.com/user-attachments/assets/3d889b8f-d063-4746-ab39-c81b114995ba" />
### 会员购-订单页面：显示所购买的物品
<img width="691" height="589" alt="image" src="https://github.com/user-attachments/assets/c2d3a1f0-f99e-42b6-9686-221340cc4725" />
### Admin管理员后台：进行一系列账号内容操作
<img width="691" height="341" alt="image" src="https://github.com/user-attachments/assets/03a30d3e-e535-4df7-93f6-94fc99c2f5fb" />
### Admin管理员后台-视频管理：可进行所有视频查看以及删除
<img width="692" height="468" alt="image" src="https://github.com/user-attachments/assets/4ada620d-ab96-44e2-9d4c-241e6af30844" />
### Admin管理员后台-用户管理：可进行所有账号的禁用与恢复
<img width="691" height="237" alt="image" src="https://github.com/user-attachments/assets/b7ffc24b-912b-4d27-a0cf-c7b58e597771" />
### Admin管理员后台-评论管理：可删除所有评论
<img width="692" height="306" alt="image" src="https://github.com/user-attachments/assets/577569f9-a32c-41ab-94af-006d6f8c161d" />
### Admin管理员后台-系统设置：账户设置
<img width="691" height="352" alt="image" src="https://github.com/user-attachments/assets/3365c7fa-46c6-4820-8f81-fc8bb8aa6873" />
