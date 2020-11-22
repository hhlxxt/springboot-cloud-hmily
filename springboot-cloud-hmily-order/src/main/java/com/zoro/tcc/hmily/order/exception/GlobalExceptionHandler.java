package com.zoro.tcc.hmily.order.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author zoro
 * @version 1.0
 * @date 2020/11/22 16:00
 * @desc
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**-------- 通用异常处理方法 --------**/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultData error(Exception e) {
        e.printStackTrace();
        //通用异常结果
        if(StringUtils.isNotBlank(e.getMessage())){
            return ResultData.error().message(e.getMessage());
        }
        return ResultData.error();
    }

    /**-------- 指定异常处理方法 --------**/
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResultData error(NullPointerException e) {
        e.printStackTrace();
        return ResultData.setResult(ResultCodeEnum.NULL_POINT);
    }

}
