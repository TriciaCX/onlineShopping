package com.shoppingproject.controller;

/**
 * Create by Tricia on 2019/5/11
 */

import com.shoppingproject.controller.viewobject.UserVO;
import com.shoppingproject.error.BussinesException;
import com.shoppingproject.error.EmBusinessError;
import com.shoppingproject.response.CommonReturnType;
import com.shoppingproject.service.UserService;
import com.shoppingproject.service.model.UserModel;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") //需要配合前端设置xhrFileds授信后使得跨域session共享
public class UserController extends BaseController{

    @Autowired
    private  UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户注册接口
    @RequestMapping(value = "/register", method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telephone")String telephone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender")Byte gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password")String password) throws BussinesException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telephone); //我们之前把用户对应的otpCode放在httpservletsession里面了
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){ //durid这个方法里面对null做了处理了，两个都是null也认为是相等
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码错误！");
        }
        //开始用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender((byte)gender.intValue());
        userModel.setAge(age);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);

        return CommonReturnType.create(null);
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法,加密字符串

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest(str.getBytes("utf-8")));
    }



    //用户获取otp短信接口
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone")String telephone){
        //String otpCode = RandomStringUtils.randomNumeric(6);

        //按照一定的规则生成otp验证码
        Random random = new Random();
        int randomInt =  random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将otp验证码同对应用户的手机号关联,企业一般用redis，我们暂时用httpsession的方式绑定用户的手机号和otpcode
        httpServletRequest.getSession().setAttribute(telephone,otpCode);

        //将otp通过短信通道发送给用户（这里先不做惹，要钱）
        System.out.println("telephone="+telephone + " optCode="+otpCode);  //为了方便调试，这里直接打印输出了，在实际应用中不要用，暴露了用户敏感信息！

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id")Integer id) throws BussinesException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if(userModel == null){
            //userModel.setEncrptPassword("123"); 测试handlerException方法
          throw new BussinesException(EmBusinessError.UER_NOT_EXIST);
        }

        //不能直接把userModel返回给前端，会泄露password等隐私信息！所以我们转化成userVO
        //将核心领域模型用户对象转化成可供UI使用的view object
        UserVO userVO = convertFromModel(userModel);
        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }else{
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userModel,userVO);
            return userVO;
        }
    }



}
