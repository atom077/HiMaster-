package com.whohim.springboot.common;


public enum ResponseCode {

    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NONE_TOKEN(10, "NONE_TOKEN"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT"),
    ILLEGAL_TOKEN(5, "ILLEGAL_TOKEN"),
    NEED_RElOGIN(3, "NEED_RElOGIN");

    private final int code;
    private final String desc;


    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
