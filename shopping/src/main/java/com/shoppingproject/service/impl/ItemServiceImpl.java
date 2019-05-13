package com.shoppingproject.service.impl;

import com.shoppingproject.dao.ItemDOMapper;
import com.shoppingproject.dao.ItemStockDOMapper;
import com.shoppingproject.dataobject.ItemDO;
import com.shoppingproject.dataobject.ItemStockDO;
import com.shoppingproject.error.BussinesException;
import com.shoppingproject.error.EmBusinessError;
import com.shoppingproject.service.ItemService;
import com.shoppingproject.service.PromoService;
import com.shoppingproject.service.model.ItemModel;
import com.shoppingproject.service.model.PromoModel;
import com.shoppingproject.validator.ValidationResult;
import com.shoppingproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());

        return  itemStockDO;
    }


    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BussinesException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //转化itemModel->dataobject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);

        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

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

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null){
            return null;
        }
        //操作获得库存数量
        ItemStockDO itemStockDO =  itemStockDOMapper.selectByItemId(itemDO.getId());

        //将dataobject转化成model
        ItemModel itemModel = convertModelFromDataObject(itemDO,itemStockDO);

        //获取活动商品信息
        PromoModel promoModel= promoService.getPromoByItemId(itemModel.getId());
        //存在还没结束的秒杀活动（未开始+正在进行）
        if(promoModel != null && promoModel.getStatus().intValue()!=3){
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }
    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BussinesException {
        int affectedRow = itemStockDOMapper.decreaseStock(itemId,amount);  //该操作影响的条目数，如果没有减成功，affectRows=0
        if(affectedRow>0){
            //更新库存成功
            return true;
        }else {
            //更新库存失败
            //throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"更新库存失败");
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BussinesException {
        itemDOMapper.increaseSales(itemId,amount);
    }
}
