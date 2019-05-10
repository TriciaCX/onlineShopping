package com.miaoshaproject;

import com.miaoshaproject.dao.UserDaoMapper;
import com.miaoshaproject.dataobject.UserDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  onlineShopping
 */
@SpringBootApplication(scanBasePackages = {"com.miaoshaproject"})
@RestController
@MapperScan("com.miaoshaproject.dao")
public class App
{
   @Autowired
   private  UserDaoMapper userDaoMapper;

   @RequestMapping("/")
   private String home(){
       UserDao userDao = userDaoMapper.selectByPrimaryKey(1);
       if(userDao==null){
           return "用户对象不存在";
       }else{
           return userDao.getName();
       }
   }

    public static void main( String[] args )
    {
        System.out.println( "程序开启!" );
        SpringApplication.run(App.class,args);
    }
}
