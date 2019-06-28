package com.shoppingproject.service.impl;

import com.shoppingproject.dao.UserDOMapper;
import com.shoppingproject.dao.UserPasswordDOMapper;
import com.shoppingproject.dataobject.UserDO;
import com.shoppingproject.dataobject.UserPasswordDO;
import com.shoppingproject.error.BussinesException;
import com.shoppingproject.error.EmBusinessError;
import com.shoppingproject.service.UserService;
import com.shoppingproject.service.model.UserModel;
import com.shoppingproject.validator.ValidationResult;
import com.shoppingproject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * Create by Tricia on 2019/5/11
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public UserModel getUserById(Integer id) {
        //调用userDoMapper获取到对应的用户dataObject, UserDO是不能直接给前端的！
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO == null){
            return  null;
        }
        //通过用户id获取对应的用户加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObject(userDO,userPasswordDO);
    }


    /**
     * 6.27更新：增加从缓存里读取用户信息
     * @param id
     * @return
     */
    @Override
    public UserModel getUserByIdInCache(Integer id) {
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get("user_validate_"+id);
        if(userModel==null){
            userModel = this.getUserById(id);
            redisTemplate.opsForValue().set("user_validate_"+id,userModel);
            redisTemplate.expire("user_validate_"+id,10, TimeUnit.MINUTES);
        }
        return userModel;
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BussinesException {
        if(userModel == null){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

//        if(StringUtils.isEmpty(userModel.getName())
//                ||userModel.getGender()==null
//                ||userModel.getAge()==null
//                ||StringUtils.isEmpty(userModel.getTelephone())){
//            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }

        ValidationResult result =  validator.validate(userModel);
        if(result.isHasErrors()){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //实现model转到dataobject的方法
        UserDO userDO = convertFromModel(userModel);
        try{
            userDOMapper.insertSelective(userDO);   //为什么用insertSelective不直接用insert？ insertSelective碰到null会采用数据库的默认值，不会再更新它
        }catch (DuplicateKeyException ex){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"该手机号已注册！");
        }




        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = converPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
        return;
    }

    @Override
    public UserModel validateLogin(String telephone, String encrptPassword) throws BussinesException {
        //通过用户手机获取用户信息
         UserDO userDO = userDOMapper.selectByTelephone(telephone);
         if(userDO == null){
             throw new BussinesException(EmBusinessError.UER_LOGIN_FAIL);
         }
         UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
         UserModel userModel = convertFromDataObject(userDO,userPasswordDO);
        //比对用户信息内加密的密码是否和传输进来的密码相匹配
       if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
           throw new BussinesException(EmBusinessError.UER_LOGIN_FAIL);
       }
       return userModel;
    }

    private  UserPasswordDO converPasswordFromModel(UserModel userModel){
        if(userModel == null){
            return  null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO != null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }

        return userModel;
    }
}
