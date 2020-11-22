package com.zoro.tcc.hmily.order.exception;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zoro
 * @version 1.0
 * @date 2020/11/22 16:02
 * @desc
 */
@Data
public class ResultData {
    private Boolean success;

    private Integer code;

    private String message;

    private Map<String, Object> data = new HashMap<>();

    /**
     * 构造器私有
     */
    private ResultData(){}

    /**
     * 通用返回成功
     * @return
     */
    public static ResultData ok() {
        ResultData r = new ResultData();
        r.setSuccess(ResultCodeEnum.SUCCESS.getSuccess());
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return r;
    }

    /**
     * 通用返回失败，未知错误
     * @return
     */
    public static ResultData error() {
        ResultData r = new ResultData();
        r.setSuccess(ResultCodeEnum.ERROR.getSuccess());
        r.setCode(ResultCodeEnum.ERROR.getCode());
        r.setMessage(ResultCodeEnum.ERROR.getMessage());
        return r;
    }

    /**
     * 设置结果，形参为结果枚举
     * @param result
     * @return
     */
    public static ResultData setResult(ResultCodeEnum result) {
        ResultData r = new ResultData();
        r.setSuccess(result.getSuccess());
        r.setCode(result.getCode());
        r.setMessage(result.getMessage());
        return r;
    }

    /**------------使用链式编程，返回类本身-----------**/

    /**
     * 自定义返回数据
     * @param map
     * @return
     */
    public ResultData data(Map<String,Object> map) {
        this.setData(map);
        return this;
    }

    /**
     * 通用设置data
     * @param key
     * @param value
     * @return
     */
    public ResultData data(String key,Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * 自定义状态信息
     * @param message
     * @return
     */
    public ResultData message(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * 自定义状态码
     * @param code
     * @return
     */
    public ResultData code(Integer code) {
        this.setCode(code);
        return this;
    }

    /**
     * 自定义返回结果
     * @param success
     * @return
     */
    public ResultData success(Boolean success) {
        this.setSuccess(success);
        return this;
    }
}
