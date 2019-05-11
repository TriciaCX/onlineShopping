package com.shoppingproject.error;

/**
 * Create by Tricia on 2019/5/11
 */

//包装器业务异常类实现
public class BussinesException extends Exception implements CommonError{

    private CommonError commonError; //强关联一个对应的commonError

    //直接接收EmBusinessError的传参，用户构造业务异常
    public BussinesException(CommonError commonError){
        super();
        this.commonError = commonError;
    }

    //接收自定义errMsg的方式构造业务异常
    public BussinesException(CommonError commonError,String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
