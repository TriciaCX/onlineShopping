package com.shoppingproject.service;

import com.shoppingproject.error.BussinesException;
import com.shoppingproject.service.model.UserModel;

public interface UserService {
    //通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BussinesException;
}
