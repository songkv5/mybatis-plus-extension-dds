package com.ws.mybatis.plus.enums;

/**
 * @author willis<songkai01>
 * @chapter 路由规则
 * @section
 * @since 2020年01月04日 10:55
 */
public enum RouteRule {
    // 主库
    ROUTE_MASTER("MASTER"),
    // 从库
    ROUTE_SLAVE("SLAVE"),
    // 默认规则
    DEFAULT("DEFAULT");

    private String name;

    RouteRule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
