![](https://img.shields.io/badge/language-Java-orange.svg)
[![](https://img.shields.io/badge/CSDN-@冰美式-yellow.svg)](https://me.csdn.net/weixin_43277507)
[![](https://img.shields.io/badge/website-Cloud-blue.svg)](http://106.15.72.228/resources/listitem.html)
#  本地主机

## 1 基础功能
* 用户获取短信验证码注册，利用账号名称和账号密码验证登录
* 用户进入商品推荐页面，商品列表页推荐秒杀商品
* 商品界面会显示未开始的秒杀活动（显示开始倒计时）和正在进行的秒杀活动
* 进入商品详情页可获取商品详情，普通商品（非秒杀类商品）可直接购买；秒杀类商品需要在秒杀开始后购买，用户下单享受秒杀价格，但存在数量限购
* 下单页面可进行支付，支付成功后刷新页面可即时看到商品销量的增加和库存的减少

## 2 系统设计图

* 系统设计框图

<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E7%94%B5%E5%95%86%E8%AE%BE%E8%AE%A1%E6%9E%B6%E6%9E%84%E5%9B%BE.png" width = 70% height = 70% div align=centre />

* 数据库设计图

<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E6%95%B0%E6%8D%AE%E5%BA%93%E8%AE%BE%E8%AE%A1.jpg" width = 70% height = 70% div align=centre />

## 3 结果浏览

* 用户注册
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E7%94%A8%E6%88%B7%E6%B3%A8%E5%86%8C.bmp" width = 50% height = 50% div align=centre />

* 创建商品
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E5%95%86%E5%93%81%E5%88%9B%E5%BB%BA.bmp" width = 50% height =50% div align=centre />

* 商品浏览
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E5%95%86%E5%93%81%E6%B5%8F%E8%A7%88.bmp" width = 100% height = 100% div align=centre />

* 秒杀活动
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E7%A7%92%E6%9D%80%E6%B4%BB%E5%8A%A8.bmp" width = 50% height = 50% div align=centre />
  
* 下单成功
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E4%B8%8B%E5%8D%95.png" width = 50% height = 50% div align=centre />


# 二、云端部署

<img src ="https://github.com/TriciaCX/resources/blob/master/%E9%98%BF%E9%87%8C%E4%BA%91.jpg" width = 30% height = 30% div align=centre />

## 分布式扩展

* nginx反向代理负载均衡

* 分布式会话管理

* redis实现分布式会话存储

采用四台阿里云服务器，其中，一台为Nginx反向代理服务器，两台为项目应用服务器（可水平扩展至多台），一台为数据库服务器（搭载Redis）

<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6.jpg" width = 70% height = 70% div align=centre />



## 多级缓存

* Redis缓存
   
   商品详情页、秒杀活动
   
* Guava cache

   

* Nginx proxy cache
   
   该项实测比从shopping 服务器读文件更慢了，弃

* Nginx lua

* CDN
  
  理论上可以利用CDN，将静态化页面存储在离用户更近的地方（实际没有完成测试，有点贵，等之后调完了再接）
  

## 一致性问题


