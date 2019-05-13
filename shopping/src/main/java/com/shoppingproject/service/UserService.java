package com.shoppingproject.service;

/**
 * Create by Tricia on 2019/5/11
 */

import com.shoppingproject.error.BussinesException;
import com.shoppingproject.service.model.UserModel;

public interface UserService {
    //通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BussinesException;
    /*
    telephone： 用户注册手机
    password：  用户加密后的密码
     */
    UserModel validateLogin(String telephone, String encrptPassword) throws BussinesException;
}
