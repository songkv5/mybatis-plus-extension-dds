package com.ws.mybatis.plus.db;

import com.ws.mybatis.plus.enums.DBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2019年11月07日 14:23
 */
public class DsRouteContextHolder {
    /**
     * 当前从库数据源切换次数
     */
    public static final AtomicLong SLAVE_CHANGE_COUNTER;
    private static final ThreadLocal<DataSourceRouteContext> ROUTE_CONTEXT;
    private static final Logger LOGGER;

    static {
        ROUTE_CONTEXT = new ThreadLocal<>();
        SLAVE_CHANGE_COUNTER = new AtomicLong(0L);
        LOGGER = LoggerFactory.getLogger(DsRouteContextHolder.class);
    }
    static {
        DataSourceRouteContext.Builder builder = DataSourceRouteContext.newBuilder();
        // 默认规则，主库，不强制
        set(builder.crtDbType(DBType.MASTER).force(false).build());
    }
    public static void set(DataSourceRouteContext dataSourceRouteContext) {
        ROUTE_CONTEXT.set(dataSourceRouteContext);
    }

    public static DataSourceRouteContext get() {
        return ROUTE_CONTEXT.get();
    }

    /**
     * 设为主库
     * @param force 是否强制使用主库。true = 强制，不允许切换； false = 不强制，可以切换数据源
     */
    public static void master(boolean force) {
        DataSourceRouteContext.Builder builder = DataSourceRouteContext.newBuilder();
        set(builder.crtDbType(DBType.MASTER).force(force).build());
    }
    public static void master() {
        master(false);
    }

    /**
     * 默认为了保证可用，使用主库。
     * 可以任意切换数据源，遵循读走从库，写走主库的原则
     */
    public static void defaults(){
        DataSourceRouteContext.Builder builder = DataSourceRouteContext.newBuilder();
        set(builder.crtDbType(DBType.MASTER).force(false).build());
    }

    /**
     * 设为从库
     * @param force 是否强制使用从库，对纯查询有效。true = 强制，不允许切换； false = 不强制，可以切换数据源
     */
    public static void slave(boolean force) {
        DataSourceRouteContext.Builder builder = DataSourceRouteContext.newBuilder();
        set(builder.crtDbType(DBType.SLAVE).force(force).build());
        long count = SLAVE_CHANGE_COUNTER.getAndIncrement();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("从库切换次数={}", count);
        }
    }
    public static void slave() {
        slave(false);
    }
    /**
     * 清除上下文数据, 设为默认
     */
    public static void clearDsRouteCxt() {
        defaults();
    }
}