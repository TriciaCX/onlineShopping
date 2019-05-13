package com.shoppingproject.service.model;

import java.math.BigDecimal;

/**
 * Create by Tricia on 2019/5/12
 */

//用户下单的交易模型
public class OrderModel {
    //订单编号
    private String id;

    //用户id
    private Integer userId;

    //购买的商品id
    private Integer itemId;

    //若不为空，则表示是在秒杀活动中下单的
    private Integer promoId;

    //购买时的商品价格(新增，考虑到商品价格可能不断变化，我们应该以用户购买时的商品价格为准)
    //若promoId不为空，则表示秒杀活动中的商品价格
    private BigDecimal itemPrice;

    //购买数量
    private Integer amount;

    //购买金额
    //若promoId不为空，则表示秒杀活动中的订单交易价格
    private BigDecimal orderPrice;





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }


    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getorderPrice() {
        return orderPrice;
    }

    public void setorderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }
}
