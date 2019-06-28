package com.shoppingproject.service.impl;

import com.shoppingproject.dao.ItemDOMapper;
import com.shoppingproject.dao.ItemStockDOMapper;
import com.shoppingproject.dataobject.ItemDO;
import com.shoppingproject.dataobject.ItemStockDO;
import com.shoppingproject.error.BussinesException;
import com.shoppingproject.error.EmBusinessError;
import com.shoppingproject.mq.MqProducer;
import com.shoppingproject.service.ItemService;
import com.shoppingproject.service.PromoService;
import com.shoppingproject.service.model.ItemModel;
import com.shoppingproject.service.model.PromoModel;
import com.shoppingproject.validator.ValidationResult;
import com.shoppingproject.validator.ValidatorImpl;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Create by Tricia on 2019/5/11
 */

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer mqProducer;

    /**
     * 将itemModel转化为itemDO
     * @param itemModel
     * @return
     */
    private ItemDO  convertItemDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO); //BeanUtils不会copy不一致的对象
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        //为什么不直接在itemModel里把price设为double?因为double类型的数传到前端，可能会出现1.9变成1.999的情况！

        return itemDO;
    }

    /**
     * 将取到的itemModel转为DO
     * @param itemModel
     * @return
     */
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());

        return  itemStockDO;
    }


    /**
     * 创建商品，涉及到数据库的更新
     * @param itemModel
     * @return
     * @throws BussinesException
     */
    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BussinesException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //转化itemModel->dataObject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);

        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    /**
     * 商品列表显示
     * @return
     */
    @Override
    public List<ItemModel> listItem() {
       List<ItemDO> itemDOList = itemDOMapper.listItem();
       List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
           ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
           ItemModel itemModel = this.convertModelFromDataObject(itemDO,itemStockDO);
           return itemModel;
       }).collect(Collectors.toList());
       return itemModelList;
    }

    /**
     *  取商品操作
     *  根据商品的id从数据库获得DO，然后由DO转成model
     * @param id
     * @return
     */
    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null){
            return null;
        }
        //操作获得库存数量
        ItemStockDO itemStockDO =  itemStockDOMapper.selectByItemId(itemDO.getId());

        //将dataObject转化成model
        ItemModel itemModel = convertModelFromDataObject(itemDO,itemStockDO);

        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        //存在还没结束的秒杀活动（未开始+正在进行）
        if(promoModel != null && promoModel.getStatus().intValue()!=3){
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }

    /**
     * 0627更新：增加了redis缓存的存取
     * @param id
     * @return
     */
    @Override
    public ItemModel getItemByIdInCache(Integer id) {
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_validate_"+id);
        if(itemModel==null){
            itemModel = this.getItemById(id);
            redisTemplate.opsForValue().set("item_validate_"+id,itemModel);
            //缓存要记得设置有效期！！！
            redisTemplate.expire("item_validate_"+id,10, TimeUnit.MINUTES);
        }
        return itemModel;
    }

    /**
     * 把拿到的dataObject转化为itemModel
     * @param itemDO
     * @param itemStockDO
     * @return
     */
    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }

    /**
     * 减库存操作，直接用数据库的命令就行
     * 减库存会受到库存的制约，返回true表示减库存成功，false表示减库存失败
     * @param itemId
     * @param amount
     * @return
     * @throws BussinesException
     */
    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {

        /*0627更新 由于利用redis缓存数据，我们要在redis里把数据减掉，不然页面销量不会变！！*/
        //int affectedRow = itemStockDOMapper.decreaseStock(itemId,amount);  //该操作影响的条目数，如果没有减成功，affectRows=0
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * (-1));
//        if(affectedRow>0){
//            //更新库存成功
//            return true;
//        }else {
//            //更新库存失败
//            //throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"更新库存失败");
//            return false;
//        }
        if (result >= 0) {
            //东西还有，库存更新成功
            boolean mqResult = mqProducer.asyncReduceStock(itemId, amount);
            if (!mqResult) {
                redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
                return false;
            }
            return true;
        } else {
            //库存没了，库存更新失败
            redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
            return false;
        }
    }


    /**
     * 加库存操作，理论上，加的操作是不会失败的
     * @param itemId
     * @param amount
     * @throws BussinesException
     */
    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BussinesException {
        itemDOMapper.increaseSales(itemId,amount);
    }
}
