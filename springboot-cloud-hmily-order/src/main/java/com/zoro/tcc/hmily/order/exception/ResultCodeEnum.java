package com.zoro.tcc.hmily.order.exception;

/**
 * @author zoro
 * @version 1.0
 * @date 2020/11/22 16:04
 * @desc
 */
public enum  ResultCodeEnum {

    SUCCESS(true,200,"成功"),
    ERROR(false,201,"未知异常"),
    NULL_POINT(false,204,"空指针异常");
    /**
     * 响应是否成功
     */
    private Boolean success;
    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ResultCodeEnum(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}
