package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDaoMapper;
import com.miaoshaproject.dao.UserPasswordDaoMapper;
import com.miaoshaproject.dataobject.UserDao;
import com.miaoshaproject.dataobject.UserPasswordDao;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
   @Autowired
   private UserDaoMapper userDaoMapper;

   @Autowired
   private UserPasswordDaoMapper userPasswordDaoMapper;

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(StringUtils.isEmpty(userModel.getName())
                || userModel.getGender()==null
                || userModel.getAge()==null
                || StringUtils.isEmpty(userModel.getTelphone())){
           throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //实现model->dataobject的方法
        UserDao userDao = convertFromModel(userModel);
        userDaoMapper.insertSelective(userDao);

        userModel.setId(userDao.getId());

        UserPasswordDao userPasswordDao = convertPasswordFromModel(userModel);
        userPasswordDaoMapper.insertSelective(userPasswordDao);

        return;
    }

    private UserPasswordDao convertPasswordFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserPasswordDao userPasswordDao = new UserPasswordDao();
        userPasswordDao.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDao.setUserId(userModel.getId());
        return userPasswordDao;
    }

    private UserDao convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserDao userDao = new UserDao();
        BeanUtils.copyProperties(userModel,userDao);
        return userDao;
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
}
