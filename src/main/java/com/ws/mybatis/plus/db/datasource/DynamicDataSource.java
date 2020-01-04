package com.ws.mybatis.plus.db.datasource;

import com.ws.mybatis.plus.db.DataSourceRouteContext;
import com.ws.mybatis.plus.db.DsRouteContextHolder;
import com.ws.mybatis.plus.enums.DBType;
import com.ws.mybatis.plus.exception.DdsException;
import com.ws.mybatis.plus.exception.ExceptionCode;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author willis<songkai01>
 * @chapter 动态数据源
 * @section
 * @since 2020年01月02日 20:05
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    /**
     * 主库
     */
    private DataSource master;
    /**
     * 从库
     */
    private List<DataSource> slaves;

    /**
     * 从库节点数
     */
    private static final AtomicInteger SLAVE_NUM;
    static {
        SLAVE_NUM = new AtomicInteger(0);
    }

    private DynamicDataSource() {
    }

    /**
     * 构建器
     * @return
     */
    public static final Builder newBuilder() {
        return new Builder();
    }
    public static class Builder{
        private DynamicDataSource dds;

        public Builder() {
            dds = new DynamicDataSource();
        }

        /**
         * 主库数据源
         * @param dataSource
         * @return
         */
        public Builder master(DataSource dataSource) {
            dds.master = dataSource;
            return this;
        }

        /**
         * 加一个从库
         * @param dataSource
         * @return
         */
        public Builder addSlave(DataSource dataSource) {
            if (dds.slaves == null) {
                dds.slaves = new ArrayList<>();
            }
            if (!dds.slaves.contains(dataSource)) {
                dds.slaves.add(dataSource);
                SLAVE_NUM.getAndIncrement();
            }
            return this;
        }
        public DynamicDataSource build() {
            if (dds.master == null) {
                throw new DdsException(ExceptionCode.MASTER_NODE_MISSING, "缺少主库节点！");
            }
            Map<Object, Object> targetDataSources = new HashMap<>(2);
            // 配置动态数据源
            targetDataSources.put(DBType.MASTER.getName(), dds.master);
            if (!CollectionUtils.isEmpty(dds.slaves)) {
                for (int i = 0; i < SLAVE_NUM.get(); i ++) {
                    /**
                     * key = SLAVE + i
                     */
                    String key = DBType.SLAVE.getName() + i;
                    targetDataSources.put(key, dds.slaves.get(i));
                }
            } else {
                // 如果没有从库，主从保持一致
            }
//            DynamicDataSource dynamicDataSource = new DynamicDataSource();
            dds.setDefaultTargetDataSource(dds.master);
            dds.setTargetDataSources(targetDataSources);
            return this.dds;
        }
    }

    /**
     * 路由规则
     * 主 or 从
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceRouteContext dataSourceRouteContext = DsRouteContextHolder.get();
        DBType crtDbType = dataSourceRouteContext.getCrtDbType();
        return DataSourceRouteContext.routeKey(crtDbType, SLAVE_NUM.get());
    }
}