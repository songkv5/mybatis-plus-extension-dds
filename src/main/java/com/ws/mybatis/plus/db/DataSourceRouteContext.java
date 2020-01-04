package com.ws.mybatis.plus.db;

import com.ws.mybatis.plus.enums.DBType;
import com.ws.mybatis.plus.enums.RouteRule;
import lombok.Data;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2020年01月04日 10:25
 */
@Data
public class DataSourceRouteContext {
    /**
     * 路由规则
     */
    private RouteRule rule;
    /**
     * 数据源类型
     */
    private DBType crtDbType;
    /**
     * 强制使用数据源，不可以切换
     * 默认：可以切换
     */
    private Boolean force = false;



    private DataSourceRouteContext() {
    }

    public static Builder newBuilder() {
        return new Builder();
    }
    public static class Builder{
        /**
         * 数据源类型
         */
        private DBType crtDbType;
        /**
         * 强制使用数据源，不可以切换
         * 默认：可以切换
         */
        private Boolean force = false;

        private Builder() {
        }

        private static Builder newBuilder() {
            return new Builder();
        }

        public Builder crtDbType(DBType crtDbType) {
            this.crtDbType = crtDbType;
            return this;
        }

        public Builder force(Boolean force) {
            this.force = force;
            return this;
        }


        public DataSourceRouteContext build() {
            DataSourceRouteContext dsr = new DataSourceRouteContext();
            dsr.crtDbType = this.crtDbType;
            if (this.force == null) {
                dsr.force = false;
            } else {
                dsr.force = this.force;
            }
            return dsr;
        }
    }

    /**
     * 数据源路由key规则
     * @param dbType
     * @param slaveNum
     * @return
     */
    public static String routeKey(DBType dbType, Integer slaveNum) {
        String result = DBType.MASTER.getName();
        switch (dbType) {
            case MASTER:{
                break;
            }
            case SLAVE:{
                // 当前从库切换次数
                if (slaveNum == null || slaveNum == 0) {
                    //routekey 不变
                } else {
                    long num = DsRouteContextHolder.SLAVE_CHANGE_COUNTER.get();
                    // slave index，为了让每次切换从库数据源能够均匀分配
                    long index = num % slaveNum;
                    /**
                     * key style = SLAVE+i
                     * eg. SLAVE1, SLAVE2
                     */
                    result = DBType.SLAVE.getName() + index;
                }
                break;
            }
            default:{
                break;
            }
        }
        return result;
    }
}