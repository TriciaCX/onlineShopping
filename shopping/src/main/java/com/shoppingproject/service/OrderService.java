package com.shoppingproject.service;

import com.shoppingproject.error.BussinesException;
import com.shoppingproject.service.model.OrderModel;


/**
 * Create by Tricia on 2019/5/11
 */
public interface OrderService {
    //1、通过前端url上传过来秒杀活动id，然后下单校验对应id是否属于对应商品且活动已开始

    //2、直接在下单接口内判断对应商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单
    // 【不合理之处】
    // 1）如果活动存在不同的页面，譬如有的用户从秒杀界面买了，有的用户没有
    // 2）如果在后端每次都要判断一下，那么普通股商品也要走一遍判断流程，性能下降
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BussinesException;


}
