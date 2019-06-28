package com.shoppingproject.service.impl;

import com.shoppingproject.dao.OrderDOMapper;
import com.shoppingproject.dao.SequenceDOMapper;
import com.shoppingproject.dataobject.OrderDO;
import com.shoppingproject.dataobject.SequenceDO;
import com.shoppingproject.error.BussinesException;
import com.shoppingproject.error.EmBusinessError;
import com.shoppingproject.mq.MqProducer;
import com.shoppingproject.service.ItemService;
import com.shoppingproject.service.OrderService;
import com.shoppingproject.service.UserService;
import com.shoppingproject.service.model.ItemModel;
import com.shoppingproject.service.model.OrderModel;
import com.shoppingproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单服务类
 */

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private MqProducer mqProducer;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BussinesException {
        System.out.println("开始建立订单了");
        //1 校验下单状态：下单的商品是否存在；下单用户是否合法；购买数量是否正确。
        // ItemModel itemModel = itemService.getItemById(itemId);
        /* 0627更新，改为利用redis取itemModel*/
       ItemModel itemModel = itemService.getItemByIdInCache(itemId);

        if(itemModel == null){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }

        /* 0627更新  增加redis缓存的用户模型读取*/
        //UserModel userModel = userService.getUserById(userId);
        UserModel userModel = userService.getUserByIdInCache(userId);

        if(userModel == null){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
        }

        if(amount<=0||amount>99){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"购买数量不合理");
        }

        //2019/5/13新增
        //校验活动信息
        if(promoId!=null){
                //(1)校验对应活动是否存在于这个商品
            if(promoId.intValue()!=itemModel.getPromoModel().getId()){
                throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
                //(2)校验活动是否在进行中
            }else if(itemModel.getPromoModel().getStatus().intValue()!=2){
                throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }

        //2 落单减库存，（支付减库存会给用户带来不好的购物体验）
         boolean hasStock = itemService.decreaseStock(itemId,amount);
         if(!hasStock ){
             throw new BussinesException(EmBusinessError.STOCK_NOT_ENOUGH);
         }

        //3 订单入库
         OrderModel orderModel = new OrderModel();
         orderModel.setUserId(userId);
         orderModel.setItemId(itemId);
         orderModel.setAmount(amount);
         //订单价格分为秒杀活动时的价格和平销价格
         if(promoId == null){
             orderModel.setItemPrice(itemModel.getPrice());
         }else {
             orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
         }
         orderModel.setPromoId(promoId);
         orderModel.setorderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
         //生成交易流水号
         orderModel.setId(generateOrderNo());

         OrderDO orderDO = convertDOFromOrderModel(orderModel);
         orderDOMapper.insertSelective(orderDO);

         //商品销量增加
        System.out.println("加销量了");
        itemService.increaseSales(itemId,amount);

        //4 返回前端
        return  orderModel;
    }

   @Transactional(propagation = Propagation.REQUIRES_NEW)  //这样设置，是为了在用户提交订单失败后，流水号仍然自增下去，不被回滚，保证全局只有唯一一个流水号
    protected String generateOrderNo(){

        //订单号16位，前8位为时间信息（年月日），中间6位为自增序列，最后两位为分库分表位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息（年月日）
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        //中间6位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i<6-sequenceStr.length(); i++){ //凑足6位
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);
        //注意，这里存在一个问题，一直加下去，会不会超出？后期需要在数据库里设置一个最大值，超出后就进行重置

        //最后两位为分库分表位，暂时我们写成固定值
        stringBuilder.append("00");
        //测试
         System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    private OrderDO convertDOFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getorderPrice().doubleValue());
        return orderDO;
    }




}
