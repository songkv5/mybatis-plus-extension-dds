package com.ws.mybatis.plus.enums;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2019年11月07日 14:24
 */
public enum DBType {
    // 主库
    MASTER("MASTER"),
    // 从库
    SLAVE("SLAVE"),
    ;
    private String name;

    DBType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}