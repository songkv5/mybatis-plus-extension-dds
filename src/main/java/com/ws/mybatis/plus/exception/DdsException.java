package com.ws.mybatis.plus.exception;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2020年01月02日 20:17
 */
public class DdsException extends RuntimeException {
    private int code;
    private String msg;

    public DdsException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public DdsException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}