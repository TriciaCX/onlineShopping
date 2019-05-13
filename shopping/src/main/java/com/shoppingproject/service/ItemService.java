package com.shoppingproject.service;

import com.shoppingproject.error.BussinesException;
import com.shoppingproject.service.model.ItemModel;

import java.util.List;

/**
 * Create by Tricia on 2019/5/11
 */
public interface ItemService {
    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BussinesException;

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId, Integer amount)throws BussinesException;

    //商品销量增加
    void increaseSales(Integer itemId,Integer amount) throws BussinesException;
}
