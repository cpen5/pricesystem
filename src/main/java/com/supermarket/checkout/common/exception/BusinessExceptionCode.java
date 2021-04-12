package com.supermarket.checkout.common.exception;

public enum BusinessExceptionCode {
    INVALID_RULE("not a valid rule")
    ;

    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
