package com.shoppingproject.service.impl;

import com.shoppingproject.dao.PromoDOMapper;
import com.shoppingproject.dataobject.PromoDO;
import com.shoppingproject.service.PromoService;
import com.shoppingproject.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
        //dataobject->model
        PromoModel promoModel = converFromDataObject(promoDO);
        if(promoModel == null){
            return null;
        }

        //判断当前时间秒杀活动是否即将开始或正在进行
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);  //没开始
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);  //已结束
        }else {
            promoModel.setStatus(2);  //正在进行中
        }
        return promoModel;
    }

    private PromoModel converFromDataObject(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
