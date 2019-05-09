package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDaoMapper;
import com.miaoshaproject.dao.UserPasswordDaoMapper;
import com.miaoshaproject.dataobject.UserDao;
import com.miaoshaproject.dataobject.UserPasswordDao;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
   @Autowired
   private UserDaoMapper userDaoMapper;

   @Autowired
   private UserPasswordDaoMapper userPasswordDaoMapper;

    @Override
    public UserModel getUserById(Integer id){
        //调用userDaoMapper获取到对应的用户dataobject
        UserDao userDao = userDaoMapper.selectByPrimaryKey(id);
        if(userDao == null){
            return null;
        }
        //通过用户id获取对应的用户加密密码信息
        UserPasswordDao userPasswordDao = userPasswordDaoMapper.selectByUserId(userDao.getId());

        return convertFromDataObject(userDao,userPasswordDao);
    }

    private UserModel convertFromDataObject(UserDao userDao,UserPasswordDao userPasswordDao){
       if(userDao == null)
           return  null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDao,userModel);
        if(userPasswordDao!=null)
        {
            userModel.setEncrptPassword(userPasswordDao.getEncrptPassword());
        }
        return  userModel;

    }
}
