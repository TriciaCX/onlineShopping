![](https://img.shields.io/badge/language-Java-orange.svg)
[![](https://img.shields.io/badge/CSDN-@冰美式-red.svg)](https://me.csdn.net/weixin_43277507)

# 功能简介

## 1 基础功能
* 用户获取短信验证码注册，利用账号名称和账号密码验证登录
* 用户进入商品推荐页面，商品列表页推荐秒杀商品
* 商品界面会显示未开始的秒杀活动（显示开始倒计时）和正在进行的秒杀活动
* 进入商品详情页可获取商品详情，普通商品（非秒杀类商品）可直接购买；秒杀类商品需要在秒杀开始后购买，用户下单享受秒杀价格，但存在数量限购
* 下单页面可进行支付，支付成功后刷新页面可即时看到商品销量的增加和库存的减少

## 2 系统设计图

<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E7%94%B5%E5%95%86%E8%AE%BE%E8%AE%A1%E6%9E%B6%E6%9E%84%E5%9B%BE.png" width = 80% height = 80% div align=centre />

## 3 结果浏览

* 用户注册
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E7%94%A8%E6%88%B7%E6%B3%A8%E5%86%8C.bmp" width = 60% height = 60% div align=centre />

* 创建商品
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E5%95%86%E5%93%81%E5%88%9B%E5%BB%BA.bmp" width = 60% height = 60% div align=centre />

* 商品浏览
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E5%95%86%E5%93%81%E6%B5%8F%E8%A7%88.bmp" width = 100% height = 100% div align=centre />

* 秒杀活动
<img src="https://github.com/TriciaCX/onlineShopping/blob/master/resourceForIntroduction/%E7%A7%92%E6%9D%80%E6%B4%BB%E5%8A%A8.bmp" width = 60% height = 60% div align=centre />

## 4 涉及技术

* 基于IDEA+Maven搭建SpringBoot 开发环境
* MyBatis操作数据库
* 利用阿里巴巴Druid数据池
* 基于MySQL数据库

## 5 改进方向

对秒杀活动进行改进，增加高并发设计
