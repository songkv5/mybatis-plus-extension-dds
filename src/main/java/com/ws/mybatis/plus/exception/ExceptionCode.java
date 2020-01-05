package com.ws.mybatis.plus.exception;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2020年01月02日 20:20
 */
public interface ExceptionCode {
    /**
     * 主库节点配置缺失
     */
    Integer MASTER_NODE_MISSING = 40001;
    /**
     * 数据源缺失
     */
    Integer DATA_SOURCE_MISSING = 40002;
    /**
     * 扫描路径不合法
     */
    Integer BASE_PACKAGE_ERROR = 40003;
    /**
     * 当前数据源没有权限
     */
    Integer CRT_DATA_SOURCE_NO_PERMIT = 40004;

    /**
     * 数据源配置失败
     */
    Integer DDS_CONFIG_FAILED = 40005;
}