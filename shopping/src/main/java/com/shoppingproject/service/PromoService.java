package com.shoppingproject.service;

import com.shoppingproject.service.model.PromoModel;

/**
 * Create by Tricia on 2019/5/13
 */
public interface PromoService {

    //根据itemId获取即将进行的、或正在进行的聚划算活动
    PromoModel getPromoByItemId(Integer itemId);
}
