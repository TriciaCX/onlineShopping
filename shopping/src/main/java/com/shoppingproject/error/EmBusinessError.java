package com.shoppingproject.error;

/**
 * Create by Tricia on 2019/5/11
 */

public enum EmBusinessError implements CommonError {
    //通用错误类型10001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOWN_ERROR(10002,"未知错误"),

    //20000开头为用户信息相关错误定义
    UER_NOT_EXIST(20001,"用户不存在"),
    UER_LOGIN_FAIL(20002,"用户手机号或密码不正确"),   //为什么我们不直接提示用户的手机号不存在？防止异常攻击！
    UER_NOT_LOGIN(20003,"用户没有登陆"),

    //30000开头为交易信息错误
    STOCK_NOT_ENOUGH(30001,"库存不足")
    ;

    private  EmBusinessError(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private  int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
