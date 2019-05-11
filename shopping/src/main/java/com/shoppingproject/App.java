package com.shoppingproject;

import com.shoppingproject.dao.UserDOMapper;
import com.shoppingproject.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 仿淘宝购物平台在线商城购物系统
 * @Author Tricia
 * @Version 2019.5.11
 */

@SpringBootApplication(scanBasePackages = {"com.shoppingproject"})
@RestController
@MapperScan("com.shoppingproject.dao")
public class App 
{
    @Autowired
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String home(){
        UserDO userDO = userDOMapper.selectByPrimaryKey(2);
        if(userDO==null){
            return "用户对象不存在";
        }else{
            return userDO.getName();
        }

    }
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(App.class,args);
    }
}
