package com.shoppingproject.service.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Create by Tricia on 2019/5/11
 */
public class ItemModel implements Serializable {
    private  Integer id;

    //商品名称
    @NotBlank(message = "商品名称不能为空")
    private String title;

    //商品价格
    @NotNull(message = "商品价格不能为空")
    @Min(value = 0,message = "商品价格必须大于0")
    private BigDecimal price;
    //6.23  用bigdecimal的price而不是double？double类型的数据直接传到前端可能会有精度问题

    //商品库存
    @NotNull(message = "必须填写库存")
    private Integer stock;

    //商品描述
    @NotBlank(message = "必须添加商品描述")
    private String description;

    //商品销量
    private Integer sales;

    //商品描述图片的url
    @NotBlank(message = "必须添加商品图片")
    private String imgUrl;

    //使用聚合模型
    //如果promoModel不为空，则表示其拥有还未结束（没开始+正在进行）的秒杀活动

    private PromoModel promoModel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public PromoModel getPromoModel() {
        return promoModel;
    }

    public void setPromoModel(PromoModel promoModel) {
        this.promoModel = promoModel;
    }
}
