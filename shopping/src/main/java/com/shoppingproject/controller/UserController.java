package com.shoppingproject.controller;

import com.shoppingproject.controller.viewobject.UserVO;
import com.shoppingproject.error.BussinesException;
import com.shoppingproject.error.EmBusinessError;
import com.shoppingproject.response.CommonReturnType;
import com.shoppingproject.service.UserService;
import com.shoppingproject.service.model.UserModel;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Create by Tricia on 2019/5/11
 */

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") //需要配合前端设置xhrFileds授信后使得跨域session共享
public class UserController extends BaseController{

    @Autowired
    private  UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    //用户登陆接口
    @RequestMapping(value = "/login", method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone") String telephone,
                                  @RequestParam(name = "password")String password) throws BussinesException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)
                || org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BussinesException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登陆服务，用来校验用户登陆是否合法
        UserModel userModel = userService.validateLogin(telephone,this.EncodeByMd5(password));

        //将登陆凭证加入到用户登陆成功的session内

        //修改成若用户登录验证成功后将对应的登录信息和登录凭证一起存入redis中

        //生成登录凭证token，UUID（保证全局唯一性）
        String uuidToken =  UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-","");
        //建立token和用户登录态之间的联系
        redisTemplate.opsForValue().set(uuidToken,userModel);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);  //超时时间设置为1个小时

//        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        //下发了token
        return CommonReturnType.create(uuidToken);

    }

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
