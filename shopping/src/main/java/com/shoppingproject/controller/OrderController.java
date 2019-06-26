package com.shoppingproject.controller;

import com.shoppingproject.error.BussinesException;
import com.shoppingproject.error.EmBusinessError;
import com.shoppingproject.response.CommonReturnType;
import com.shoppingproject.service.OrderService;
import com.shoppingproject.service.model.OrderModel;
import com.shoppingproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate; //6.26更新，token

    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount,
                                        @RequestParam(name = "promoId",required = false)Integer promoId) throws BussinesException {
        System.out.println("接到下单请求了");
        //Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new BussinesException(EmBusinessError.UER_NOT_LOGIN,"用户未登陆，无法购买");
        }
        //获取用户登陆信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel==null){
            throw new BussinesException(EmBusinessError.UER_NOT_LOGIN,"用户未登陆，无法购买");
        }

       // UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");
        OrderModel orderModel = orderService.createOrder(userModel.getId(),itemId,promoId,amount);
        return CommonReturnType.create(null);
    }
}
